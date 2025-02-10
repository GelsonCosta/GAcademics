# Título e Tema do Projeto

## Título: "GAcademics: Plataforma de Compartilhamento de Materiais Acadêmicos Em PDF"

## Resumo
O GAcademics é uma plataforma que permite aos usuários compartilhar e acessar materiais acadêmicos em formato PDF. O aplicativo oferece funcionalidades como upload de materiais, busca por categoria, tags, favoritos e comentários. A interface do usuário foi desenvolvida com Jetpack Compose, e os dados são gerenciados por uma API REST que realiza operações CRUD (Create, Read, Update, Delete). O aplicativo também utiliza Room Database para armazenamento local de materiais offline.

## Estrutura da Aplicação
O aplicativo foi desenvolvido utilizando Kotlin e Jetpack Compose para a interface do usuário, e Retrofit para a comunicação com a API REST. A estrutura do aplicativo é composta pelas seguintes telas:

### Telas Principais

#### SplashScreen
- Exibe o nome do aplicativo (GAcademics) com o logo do aplicativo.
- Redireciona para a tela de login ou para a tela principal (HomeScreen) se o usuário já estiver autenticado.

#### LoginScreen
- Permite que o usuário faça login com e-mail e senha.
- Validação de campos obrigatórios e feedback em caso de erro.
- Integração com a API REST para autenticação.

#### RegisterScreen
- Permite que novos usuários se cadastrem no aplicativo.
- Validação de campos obrigatórios e feedback em caso de erro.
- Integração com a API REST para cadastro de usuários.

#### HomeScreen
- Exibe uma lista de materiais acadêmicos disponíveis.
- Permite a navegação para a tela de detalhes de um material específico.
- Integração com a API REST para buscar e exibir os materiais.

#### MaterialListScreen(Home)
- Lista todos os materiais disponíveis, com opções de busca simples.
- Integração com a API REST para  e exibir os materiais.

#### SearchScreen
- Permite pesquisar com opções de busca e filtragem por categoria ou tags.
- Integração com a API REST para buscar e exibir os materiais.

#### MaterialDetailScreen
- Exibe os detalhes de um material específico, incluindo título, descrição, categoria, tags e comentários.
- Permite adicionar o material aos favoritos e adicionar/editar/excluir comentários.
- Integração com a API REST para buscar detalhes do material e gerenciar comentários.

#### MyMaterialListScreen
- Exibe uma lista de materiais enviados pelo usuário logado.
- Permite editar ou excluir materiais.
- Integração com a API REST para buscar e gerenciar os materiais do usuário.

#### UploadMaterialScreen
- Permite que o usuário faça upload de novos materiais, incluindo título, descrição, categoria, tags, capa e arquivo PDF.
- Integração com a API REST para enviar os dados do material.

#### UpdateMaterialScreen
- Permite que o usuário edite um material existente.
- Integração com a API REST para atualizar os dados do material.

#### FavoriteListScreen
- Exibe uma lista de materiais favoritados pelo usuário.
- Integração com a API REST para buscar e exibir os materiais favoritados.

#### OfflineMaterialListScreen
- Exibe uma lista de materiais baixados e armazenados localmente.
- Utiliza Room Database para persistência local dos materiais.

#### PdfReaderScreen
- Permite a visualização de materiais PDF online.
- Integração com a API REST para buscar o arquivo PDF.

#### LocalPdfReaderScreen
- Permite a visualização de materiais PDF armazenados localmente.
- Utiliza Room Database para acessar os arquivos PDF offline.

## Funcionalidades e Tecnologias Usadas

### Funcionalidades

#### Autenticação de Usuário
- Login e cadastro de usuários.
- Validação de campos e feedback em caso de erro.
- Gerenciamento de tokens JWT para autenticação.

#### Upload de Materiais
- Upload de materiais com título, descrição, categoria, tags, capa e arquivo PDF.
- Validação de campos obrigatórios e feedback em caso de erro.


#### Favoritos
- Adicionar e remover materiais dos favoritos.
- Listagem de materiais favoritados.

#### Comentários
- Adicionar, editar e excluir comentários em materiais.
- Listagem de comentários por material.

#### Armazenamento Local
- Download e armazenamento local de materiais para acesso offline.
- Utilização de Room Database para persistência local.

#### Visualização de PDF
- Visualização de materiais PDF online e offline.

### Tecnologias
- Kotlin: Linguagem principal para desenvolvimento do aplicativo Android.
- Jetpack Compose: Usado para construção das interfaces de usuário (UI).
- Retrofit: Usado para comunicação com a API REST.
- Room Database: Usado para persistência local de materiais offline.
- API REST: Criada para gerenciar as interações entre o frontend e o banco de dados remoto.
- JWT (JSON Web Token): Usado para autenticação de usuários.

## Backend: API REST
A API REST foi desenvolvida utilizando Node.js e Express, com integração ao MySQL para armazenamento de dados. A API oferece as seguintes rotas:

### Rotas Principais

#### Autenticação
- POST /register: Cadastro de novos usuários.
- POST /login: Autenticação de usuários.
- GET /profile: Obtenção do perfil do usuário autenticado.

#### Materiais
- POST /materials: Upload de novos materiais.
- GET /materials: Listagem de todos os materiais.
- GET /materials/:id: Obtenção de um material específico.
- PUT /materials/:id: Atualização de um material existente.
- DELETE /materials/:id: Exclusão de um material.

#### Favoritos
- POST /favorites: Adicionar um material aos favoritos.
- DELETE /favorites/:material_id: Remover um material dos favoritos.
- GET /favorites: Listagem de materiais favoritados.

#### Comentários
- POST /comments: Adicionar um comentário a um material.
- GET /comments/:material_id: Listagem de comentários de um material.
- PUT /comments/:id: Atualização de um comentário.
- DELETE /comments/:id: Exclusão de um comentário.

#### Busca
- GET /materials/search: Busca de materiais por título ou descrição.
- GET /materials/category/:category: Busca de materiais por categoria.
- GET /materials/tag/:tag: Busca de materiais por tag.