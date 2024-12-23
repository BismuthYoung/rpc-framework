rootProject.name = "rpc-framework"

fun defineSubProject(name: String, path: String) {
    include(name)
    project(":$name").projectDir = file(path)
}

defineSubProject("rpc-client", "rpc-client")
defineSubProject("rpc-server", "rpc-server")
defineSubProject("rpc-contract", "rpc-contract")