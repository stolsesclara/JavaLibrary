# JavaLibrary

Projeto de uma Biblioteca em Java com interface gráfica (GUI) usando Swing. Sistema completo para gerenciar livros, usuários, empréstimos e notificações de atrasos.

## 📋 Descrição

JavaLibrary é uma aplicação desktop desenvolvida em Java que implementa um sistema de gerenciamento de biblioteca com as seguintes funcionalidades:

- **Autenticação de Usuários**: Sistema de login seguro
- **Gerenciamento de Livros**: Adicionar, editar, deletar e consultar livros
- **Gerenciamento de Usuários**: Cadastro e gestão de clientes (patronos)
- **Gerenciamento de Empréstimos**: Registrar empréstimos e devoluções
- **Notificações**: Alertas automáticos para livros em atraso
- **Interface Gráfica**: Interface amigável desenvolvida com Swing

## 🛠️ Requisitos

- **Java**: JDK 21 ou superior
- **Sistema Operacional**: Windows, macOS ou Linux
- **RAM**: Mínimo 512MB (recomendado 1GB)

## 📦 Instalação

### 1. Clonar o Repositório

```bash
git clone https://github.com/stolsesclara/JavaLibrary.git
cd JavaLibrary
```

### 2. Compilar o Projeto

#### No Windows (PowerShell):
```powershell
# Criar pasta de saída
mkdir -Force out

# Gerar lista de arquivos Java
Get-ChildItem -Path src -Filter "*.java" -Recurse | % { $_.FullName } | Out-File sources.txt -Encoding ASCII

# Compilar
cmd /c "javac -d out @sources.txt"

# Criar JAR (usando o caminho do jar.exe)
$jarPath = "C:\Program Files\Java\jdk-21.0.10\bin\jar.exe"
cd out
$files = @(Get-ChildItem -Path . -Filter "*.class" -Recurse -File | Select-Object -ExpandProperty FullName)
& $jarPath cfe ..\JavaLibrary.jar library.Main $files
cd ..
```

#### No Linux/macOS:
```bash
# Usar o script de build fornecido
chmod +x build.sh
./build.sh
```

## 🚀 Executando a Aplicação

### Opção 1: Via JAR
```bash
java -jar JavaLibrary.jar
```

### Opção 2: Compilar e Executar
```bash
javac -d out src/main/java/library/*.java src/main/java/library/**/*.java
java -cp out library.Main
```

## 📂 Estrutura do Projeto

```
JavaLibrary/
├── src/main/java/library/
│   ├── Main.java                    # Ponto de entrada da aplicação
│   ├── gui/                         # Interface gráfica (Swing)
│   ├── service/                     # Serviços de negócio
│   │   ├── AuthService.java
│   │   ├── BookService.java
│   │   ├── PatronService.java
│   │   ├── LoanService.java
│   │   └── OverdueNotificationService.java
│   ├── model/                       # Classes de modelo
│   ├── repository/                  # Acesso aos dados
│   └── exception/                   # Exceções customizadas
├── data/                            # Dados persistentes (gerado ao executar)
├── out/                             # Arquivos compilados (gerado ao compilar)
├── build.sh                         # Script de build para Linux/macOS
├── sources.txt                      # Lista de arquivos Java (gerado)
└── README.md                        # Este arquivo
```

## 💾 Dados da Aplicação

Os dados da aplicação são armazenados localmente na pasta `data/` em formato texto. Esta pasta é criada automaticamente na primeira execução.

## 🔑 Login Padrão

Verifique a classe `AuthService` para conhecer as credenciais padrão configuradas no sistema.

## 🐛 Resolução de Problemas

### Erro "java: comando não encontrado"
- Certifique-se de que Java JDK 21+ está instalado
- Adicione o Java ao PATH do sistema

### Erro "javac: comando não encontrado"
- Você precisa do JDK completo (não apenas o JRE)
- Verifique se JAVA_HOME está configurado corretamente

### Erro ao compilar com `@sources.txt`
- No Windows, certifique-se de usar `cmd /c "javac -d out @sources.txt"`
- O arquivo `sources.txt` deve estar em formato ASCII (sem BOM)

## 📝 Logs e Debugging

Para ativar informações de debug, modifique o Main.java e adicione logs:

```java
System.out.println("Dados carregados de: " + DATA_DIR);
```

## 🤝 Contribuindo

Para contribuir com melhorias:

1. Faça um fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob licença [especificar licença]. Veja o arquivo LICENSE para mais detalhes.

## 👨‍💻 Autor

**Clara Stoles**

- GitHub: [@stolsesclara](https://github.com/stolsesclara)
- Repositório: [JavaLibrary](https://github.com/stolsesclara/JavaLibrary.git)

## 📞 Suporte

Para reportar bugs ou solicitar features, abra uma issue no repositório GitHub.

---

**Última atualização**: 2026-06-05
**Status**: ✅ Compilado e testado com sucesso
