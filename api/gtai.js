require("dotenv").config();
const express = require("express");
const mysql = require("mysql2");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const multer = require("multer");
const path = require("path");
const cors = require("cors");

const app = express();
app.use(express.json());
app.use(cors());
app.use("/uploads", express.static(path.join(__dirname, "uploads")));

// Configuração do MySQL
const db = mysql.createConnection({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME,
});

db.connect((err) => {
  if (err) throw err;
  console.log("Conectado ao MySQL!");
});

// Configuração do Multer (Upload de arquivos)
const storage = multer.diskStorage({
  destination: "./uploads",
  filename: (req, file, cb) => {
    cb(null, Date.now() + path.extname(file.originalname));
  },
});
const upload = multer({ storage });

// Middleware para autenticação JWT
const verifyToken = (req, res, next) => {
  const token = req.header("Authorization");
  if (!token) return res.status(401).json({ error: "Acesso negado!" });

  jwt.verify(token, process.env.JWT_SECRET, (err, decoded) => {
    if (err) return res.status(401).json({ error: "Token inválido!" });
    req.user = decoded;
    next();
  });
};

// ======================== ROTAS ========================

// Usuários
app.post("/register", async (req, res) => {
  const { name, email, password } = req.body;
  const hashedPassword = await bcrypt.hash(password, 10);

  db.query(
    "INSERT INTO users (name, email, password) VALUES (?, ?, ?)",
    [name, email, hashedPassword],
    (err, result) => {
      if (err) return res.status(400).json({ error: "Erro ao cadastrar usuário!" });

      const userId = result.insertId; // Obtém o ID do usuário recém-criado
      const token = jwt.sign({ id: userId, email }, process.env.JWT_SECRET, { expiresIn: "7d" });

      // Retornando o usuário recém-criado junto com o token
      const newUser = { id: userId, name, email };
      res.json({ token, user: newUser });
    }
  );
});



app.post("/login", (req, res) => {
  const { email, password } = req.body;
  db.query("SELECT id, name, email, password FROM users WHERE email = ?", [email], async (err, results) => {
    if (err || results.length === 0) return res.status(400).json({ error: "Usuário não encontrado!" });

    const user = results[0];
    const validPassword = await bcrypt.compare(password, user.password);
    if (!validPassword) return res.status(400).json({ error: "Senha incorreta!" });

    const token = jwt.sign({ id: user.id, email: user.email }, process.env.JWT_SECRET, { expiresIn: "7d" });

    // Removendo o campo `password` antes de retornar os dados do usuário
    delete user.password;

    res.json({ token, user });
  });
});


app.get("/users", (req, res) => {
  db.query("SELECT id, name, email, profile_pic FROM users", (err, results) => {
    if (err) return res.status(400).json({ error: "Erro ao buscar usuários!" });
    res.json(results);
  });
});
app.get("/profile", (req, res) => {
  const token = req.headers.authorization;
  if (!token) return res.status(401).json("Acesso negado!");

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    db.query("SELECT id, name, email FROM users WHERE id = ?", [decoded.id], (err, results) => {
      if (err || results.length === 0) return res.status(404).json("Usuário não encontrado!");
      res.json(results[0]);
    });
  } catch (error) {
    res.status(401).json("Token inválido!");
  }
});

app.put("/users", verifyToken, (req, res) => {
  const { name, email } = req.body;
  db.query("UPDATE users SET name = ?, email = ? WHERE id = ?", [name, email, req.user.id], (err) => {
    if (err) return res.status(400).json({ error: "Erro ao atualizar usuário!" });
    res.json({ message: "Usuário atualizado!" });
  });
});

app.delete("/users", verifyToken, (req, res) => {
  db.query("DELETE FROM users WHERE id = ?", [req.user.id], (err) => {
    if (err) return res.status(400).json({ error: "Erro ao excluir usuário!" });
    res.json({ message: "Usuário excluído!" });
  });
});

// Materiais
app.post("/materials", verifyToken, upload.fields([{ name: "file" }, { name: "cover" }]), (req, res) => {
  const { title, description, category, tags } = req.body;
  const filePath = `/uploads/${req.files["file"][0].filename}`;
  const coverPath = req.files["cover"] ? `/uploads/${req.files["cover"][0].filename}` : null;

  db.query(
    "INSERT INTO materials (user_id, title, description, cover, file_path, category, tags) VALUES (?, ?, ?, ?, ?, ?, ?)",
    [req.user.id, title, description, coverPath, filePath, category, tags],
    (err) => {
      if (err) return res.status(400).json({ error: "Erro ao salvar material!" });
      res.json({ message: "Material enviado com sucesso!" });
    }
  );
});

// Rota para obter todos os materiais com o nome do usuário criador
app.get("/materials", (req, res) => {
  const query = `
    SELECT 
      materials.*, 
      users.name AS creator_name, 
      favorites.id AS favorite_id
    FROM 
      materials
    JOIN 
      users ON materials.user_id = users.id
    LEFT JOIN 
      favorites ON materials.id = favorites.material_id;
  `;
  
  db.query(query, (err, results) => {
    if (err) return res.status(400).json({ error: "Erro ao buscar materiais!" });
    res.json(results);
  });
});


// Rota para obter um material específico pelo ID com o nome do usuário criador
app.get("/materials/:id", (req, res) => {
  const query = `
    SELECT 
      materials.*, 
      users.name AS creator_name 
    FROM 
      materials 
    JOIN 
      users ON materials.user_id = users.id 
    WHERE 
      materials.id = ?;
  `;

  db.query(query, [req.params.id], (err, results) => {
    if (err || results.length === 0) return res.status(400).json({ error: "Material não encontrado!" });
    res.json(results[0]);
  });
});


app.put("/materials/:id", verifyToken, upload.fields([{ name: "file" }, { name: "cover" }]), (req, res) => {
  const { title, description, category, tags } = req.body;
  

  let updateFields = [title, description, category, tags];
  let query = "UPDATE materials SET title = ?, description = ?, category = ?, tags = ?";
  

  if (req.files["file"]) {
    const filePath = `/uploads/${req.files["file"][0].filename}`;
    query += ", file_path = ?";
    updateFields.push(filePath);
  }
  
  if (req.files["cover"]) {
    const coverPath = `/uploads/${req.files["cover"][0].filename}`;
    query += ", cover = ?";
    updateFields.push(coverPath);
  }
  

  query += " WHERE id = ? AND user_id = ?";
  updateFields.push(req.params.id, req.user.id);

  db.query(query, updateFields, (err) => {
    if (err) return res.status(400).json({ error: "Erro ao atualizar material! " + err });
    res.json({ message: "Material atualizado com sucesso!" });
  });
});

app.get("/my-materials", verifyToken, (req, res) => {
  db.query("SELECT * FROM materials WHERE user_id = ?", [req.user.id], (err, results) => {
    if (err) return res.status(400).json({ error: "Erro ao buscar seus materiais!" });
    res.json(results);
  });
});

app.get("/ping",(req,res) => {
  return res.json({message: "sucesso"})
})


app.delete("/materials/:id", verifyToken, (req, res) => {
  db.query("DELETE FROM materials WHERE id = ? AND user_id = ?", [req.params.id, req.user.id], (err) => {
    if (err) return res.status(400).json({ error: "Erro ao excluir material!" });
    res.json({ message: "Material excluído!" });
  });
});

// Rotas para Favoritos
app.post("/favorites", verifyToken, (req, res) => {
  const { material_id } = req.body;
  const user_id = req.user.id; // Obtendo o ID do usuário autenticado

  if (!material_id) {
    return res.status(400).json({ error: "O ID do material é obrigatório!" });
  }

  const query = "INSERT INTO favorites (user_id, material_id) VALUES (?, ?)";

  db.query(query, [user_id, material_id], (err) => {
    if (err) return res.status(400).json({ error: "Erro ao adicionar aos favoritos!" });
    res.json({ message: "Material favoritado com sucesso!" });
  });
});

app.delete("/favorites/:material_id", verifyToken, (req, res) => {
  const user_id = req.user.id; // Obtendo o ID do usuário autenticado
  const material_id = req.params.material_id;

  const query = "DELETE FROM favorites WHERE user_id = ? AND material_id = ?";

  db.query(query, [user_id, material_id], (err, result) => {
    if (err) return res.status(400).json({ error: "Erro ao remover dos favoritos!" });

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: "Material não encontrado nos favoritos!" });
    }

    res.json({ message: "Material removido dos favoritos!" });
  });
});

app.get("/favorites", verifyToken, (req, res) => {
  const user_id = req.user.id; // Obtendo o ID do usuário autenticado

  const query = `
    SELECT 
      favorites.id AS favorite_id,
      materials.*, 
      users.name AS creator_name 
    FROM 
      favorites 
    JOIN 
      materials ON favorites.material_id = materials.id 
    JOIN 
      users ON materials.user_id = users.id 
    WHERE 
      favorites.user_id = ?;
  `;

  db.query(query, [user_id], (err, results) => {
    if (err) return res.status(400).json({ error: "Erro ao buscar favoritos!" });
    res.json(results);
  });
});

// Criar um comentário
app.post("/comments", verifyToken, async (req, res) => {
    try {
        const { material_id, content } = req.body;
        const user_id = req.user.id;

        if (!material_id || !content) {
            return res.status(400).json({ 
                error: "O ID do material e o conteúdo são obrigatórios!" 
            });
        }

        const query = `
            INSERT INTO comments (material_id, user_id, content) 
            VALUES (?, ?, ?)
        `;

        const [result] = await db.promise().query(query, [material_id, user_id, content]);

        // Buscar o comentário recém-criado com informações do usuário
        const selectQuery = `
            SELECT 
                c.id,
                c.material_id,
                c.user_id,
                u.name as user_name,
                c.content,
                c.created_at,
                c.updated_at
            FROM comments c
            JOIN users u ON c.user_id = u.id
            WHERE c.id = ?
        `;

        const [comments] = await db.promise().query(selectQuery, [result.insertId]);
        res.status(201).json(comments[0]);

    } catch (error) {
        console.error("Erro ao criar comentário:", error);
        res.status(500).json({ 
            error: "Erro interno ao criar comentário" 
        });
    }
});

// Listar comentários de um material específico
app.get("/comments/:material_id", async (req, res) => {
    try {
        const { material_id } = req.params;

        const query = `
            SELECT 
                c.id,
                c.material_id,
                c.user_id,
                u.name as user_name,
                c.content,
                c.created_at,
                c.updated_at
            FROM comments c
            JOIN users u ON c.user_id = u.id
            WHERE c.material_id = ?
            ORDER BY c.created_at DESC
        `;

        const [comments] = await db.promise().query(query, [material_id]);
        res.json(comments);

    } catch (error) {
        console.error("Erro ao buscar comentários:", error);
        res.status(500).json({ 
            error: "Erro ao buscar comentários" 
        });
    }
});

// Atualizar um comentário
app.put("/comments/:id", verifyToken, async (req, res) => {
    try {
        const { content } = req.body;
        const { id } = req.params;
        const user_id = req.user.id;

        if (!content) {
            return res.status(400).json({ 
                error: "O conteúdo do comentário não pode estar vazio!" 
            });
        }

        // Primeiro, verifica se o usuário é dono do comentário
        const checkQuery = `
            SELECT user_id FROM comments 
            WHERE id = ?
        `;
        
        const [comments] = await db.promise().query(checkQuery, [id]);
        
        if (comments.length === 0) {
            return res.status(404).json({ 
                error: "Comentário não encontrado" 
            });
        }

        if (comments[0].user_id !== user_id) {
            return res.status(403).json({ 
                error: "Você não tem permissão para editar este comentário" 
            });
        }

        // Atualiza o comentário
        const updateQuery = `
            UPDATE comments 
            SET content = ?
            WHERE id = ? AND user_id = ?
        `;

        await db.promise().query(updateQuery, [content, id, user_id]);

        // Busca o comentário atualizado
        const selectQuery = `
            SELECT 
                c.id,
                c.material_id,
                c.user_id,
                u.name as user_name,
                c.content,
                c.created_at,
                c.updated_at
            FROM comments c
            JOIN users u ON c.user_id = u.id
            WHERE c.id = ?
        `;

        const [updatedComment] = await db.promise().query(selectQuery, [id]);
        res.json(updatedComment[0]);

    } catch (error) {
        console.error("Erro ao atualizar comentário:", error);
        res.status(500).json({ 
            error: "Erro ao atualizar comentário" 
        });
    }
});

// Excluir um comentário
app.delete("/comments/:id", verifyToken, async (req, res) => {
    try {
        const { id } = req.params;
        const user_id = req.user.id;

        // Primeiro, verifica se o usuário é dono do comentário
        const checkQuery = `
            SELECT user_id FROM comments 
            WHERE id = ?
        `;
        
        const [comments] = await db.promise().query(checkQuery, [id]);
        
        if (comments.length === 0) {
            return res.status(404).json({ 
                error: "Comentário não encontrado" 
            });
        }

        if (comments[0].user_id !== user_id) {
            return res.status(403).json({ 
                error: "Você não tem permissão para excluir este comentário" 
            });
        }

        const deleteQuery = `
            DELETE FROM comments 
            WHERE id = ? AND user_id = ?
        `;

        await db.promise().query(deleteQuery, [id, user_id]);
        res.json({ message: "Comentário excluído com sucesso" });

    } catch (error) {
        console.error("Erro ao excluir comentário:", error);
        res.status(500).json({ 
            error: "Erro ao excluir comentário" 
        });
    }
});

// PESQUISAS
// Rota para pesquisar material por título ou descrição
app.get("/materials/search", (req, res) => {
  const { query } = req.query;
  
  if (!query) {
    return res.status(400).json({ error: "Termo de busca é obrigatório!" });
  }

  const searchQuery = `
    SELECT 
      materials.*, 
      users.name AS creator_name,
      favorites.id AS favorite_id
    FROM 
      materials
    JOIN 
      users ON materials.user_id = users.id
    LEFT JOIN 
      favorites ON materials.id = favorites.material_id
    WHERE 
      materials.title LIKE ? OR materials.description LIKE ?
    ORDER BY 
      materials.id
  `;

  const searchTerm = `%${query}%`;
  
  db.query(searchQuery, [searchTerm, searchTerm], (err, results) => {
    if (err) {
      console.error("Erro na busca:", err);
      return res.status(500).json({ error: "Erro ao buscar materiais!" });
    }
    res.json(results);
  });
});

// Rota para pesquisar material por categoria
app.get("/materials/category/:category", (req, res) => {
  const { category } = req.params;
  
  const searchQuery = `
    SELECT 
      materials.*, 
      users.name AS creator_name,
      favorites.id AS favorite_id
    FROM 
      materials
    JOIN 
      users ON materials.user_id = users.id
    LEFT JOIN 
      favorites ON materials.id = favorites.material_id
    WHERE 
      materials.category = ?
    ORDER BY 
      materials.id
  `;
  
  db.query(searchQuery, [category], (err, results) => {
    if (err) {
      console.error("Erro na busca por categoria:", err);
      return res.status(500).json({ error: "Erro ao buscar materiais por categoria!"+err.message });
    }
    res.json(results);
  });
});

// Rota para pesquisar material por tag
app.get("/materials/tag/:tag", (req, res) => {
  const { tag } = req.params;
  
  const searchQuery = `
    SELECT 
      materials.*, 
      users.name AS creator_name,
      favorites.id AS favorite_id
    FROM 
      materials
    JOIN 
      users ON materials.user_id = users.id
    LEFT JOIN 
      favorites ON materials.id = favorites.material_id
    WHERE 
      materials.tags LIKE ?
    ORDER BY 
      materials.id
  `;
  
  const searchTerm = `%${tag}%`;
  
  db.query(searchQuery, [searchTerm], (err, results) => {
    if (err) {
      console.error("Erro na busca por tag:", err);
      return res.status(500).json({ error: "Erro ao buscar materiais por tag!" });
    }
    res.json(results);
  });
});
// ======================== SERVIDOR ========================
app.listen(process.env.PORT, () => {
  console.log(`Servidor rodando em http://localhost:${process.env.PORT}`);
});
