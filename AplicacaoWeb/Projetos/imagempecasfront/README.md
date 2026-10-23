# NEXORA — catálogo de imagens de peças

Frontend em Next.js, React e TypeScript implementado a partir do arquivo Figma:
https://www.figma.com/design/wwl7m0jGLGjSBxFOCfQTlE/Untitled?node-id=0-1

## Executar

Execute os comandos dentro da pasta `imagempecasfront`:

```bash
npm install
npm run dev
```

Abra http://localhost:3000. Para conferir as seis imagens originais do Figma sem iniciar o backend, abra http://localhost:3000/Galeria?demo=1. A demonstração é identificada na tela e não grava dados.

## Rotas

- `/` e `/login`: layout de login e validação dos campos.
- `/cadastro`: layout de cadastro, validação de nome, e-mail, senha e confirmação.
- `/Galeria`: galeria conectada ao backend, busca por nome/tag e filtro PNG/JPEG/GIF.
- `/Galeria?demo=1`: exemplos do Figma, busca local e visualização ampliada.
- `/Formulario`: upload com seleção/arrastar arquivo, prévia, validação e progresso.

As URLs existentes `/Galeria` e `/Formulario` foram preservadas. Imagens abrem em um visualizador acessível com download, fechamento por Escape e retorno de foco.

## Backend

O frontend usa o backend Spring Boot `imagemPecasBack` em `http://localhost:8080` por padrão. Para alterar, copie `.env.example` para `.env.local`, configure `IMAGES_API_URL` e reinicie o Next.js.

As rotas do Next.js intermediam os endpoints existentes, evitando depender de CORS no navegador:

- `GET /api/images` → `GET /v1/images?query=...&extension=...`
- `POST /api/images` → `POST /v1/images` com multipart `name`, `tags` e `file`
- `GET /api/images/:id` → `GET /v1/images/:id`

O formulário aceita PNG, JPEG e GIF até 4 MiB, com nome e tags de até 50 caracteres, conforme o Figma. Validação no cliente e no servidor; o servidor também verifica a assinatura do arquivo. A confirmação só aparece após resposta bem-sucedida do backend. O progresso indica envio ao Next.js; ao chegar a 100%, a interface aguarda a gravação no backend.

O DTO atual do backend não retorna tags. A galeria aceita esse campo quando disponível, mas não inventa tags para registros reais; a busca por tags continua sendo feita pelo backend.

**Autenticação pendente no backend:** o projeto Java atual não possui endpoints de login/cadastro nem sessões. As telas foram implementadas com validação e aviso de indisponibilidade; não salvam nem enviam senhas e não simulam autenticação. A galeria permanece pública, como a API atual. O botão de saída retorna à tela de acesso. Para autenticação real será necessário implementar o serviço e proteger os endpoints no backend.

## Design

- Inter, pesos 400/500/600/700.
- Fundo `#f5f6f8`, superfície branca, texto `#1a1d21`, secundário `#6b7280`, azul `#1e6fd9`, azul escuro `#0d2b4e`.
- Cabeçalho de 69 px, margens de 40 px, grade de três colunas com espaçamento de 24 px; duas colunas em tablet e uma no celular.
- Cartão de login de 420 px, cadastro de 460 px e formulário de 640 px.
- Imagens e ícones originais baixados em `public/design`; nenhuma URL temporária do Figma é usada em execução.
- Os estados de erro, carregamento e sucesso são estados dos componentes, não páginas fictícias separadas.
- A imagem ampliada e a prévia sempre mostram o arquivo escolhido, mesmo que o mockup apresente outra foto de exemplo.

## Verificar

```bash
npm run lint
npm run build
npm run test:integration
```

O teste de integração requer um build pronto. Ele inicia temporariamente o Next.js e um servidor de teste em portas livres, confere busca, multipart, validações, gravação, leitura da imagem e erro do serviço. Não usa o banco de dados real. O backend real precisa estar ativo com seu PostgreSQL para usar o catálogo persistente.
