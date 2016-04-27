var env = Java.type("java.lang.System").getenv;
var uri = Java.type("java.net.URI").create;

function dockerHostOrDefault(defaultHost) {
  var dockerHost = env("DOCKER_HOST");
  return dockerHost ? uri(dockerHost).getHost() : defaultHost;
}
