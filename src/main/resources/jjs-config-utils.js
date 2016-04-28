var env = Java.type("java.lang.System").getenv;
var uri = Java.type("java.net.URI").create;

var duration = Java.type("java.time.Duration");

function dockerHostOrDefault(defaultHost) {
  var dockerHost = env("DOCKER_HOST");
  return dockerHost ? uri(dockerHost).getHost() : defaultHost;
}


function seconds(unit) {
  return duration.ofSeconds(unit);
}

function minutes(unit) {
  return duration.ofMinutes(unit);
}

function hours(unit) {
  return duration.ofHours(unit);
}

function days(unit) {
  return duration.ofDays(unit);
}


function millis(unit) {
  return duration.ofMillis(unit);
}


function milliseconds(unit) {
  return duration.ofMillis(unit);
}


