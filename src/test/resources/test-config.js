var config = {

  myUri: uri("http://host:9000/path?foo=bar"),

  someKey: {
    nestedKey: 234,
    other: "this text"
  },

  int1: 1,
  float1: 1.0,
  double1: 1.0,
  long1: 1,
  string1: "rick",
  stringList: ['Foo', 'Bar'],
  configInner: {
    int2: 2,
    float2: 2.0
  },
  uri: uri("http://localhost:8080/foo"),
  myClass: "java.lang.Object",
  myURI: "http://localhost:8080/foo",
  employee: {"id": 123, "name": "Geoff"},
  employees: [
    {id: 123, "name": "Geoff"},
    {id: 456, "name": "Rick"},
    {id: 789, 'name': "Paul"}
  ],
  floats: [1.0, 2.0, 3.0],
  doubles: [1.0, 2.0, 3.0],
  longs: [1.0, 2.0, 3.0],
  ints: [1, 2, 3],
  intsNull: [1, null, 3],
  intsWrongType: [1, "2", 3],
  tenSeconds: seconds(10),
  tenDays: days(10),
  tenMinutes: minutes(10),
  tenHours: hours(10),
  tenMillis: millis(10),
  tenMilliseconds: milliseconds(10),
  fifteenMinutes: "PT15M",
  tenSeconds2: "10 seconds",
  tenMinutes2: "10m",
  tenHours2: "10 h",
  tenDays2: "10 day",
  tenMillis2: "10ms",
  tenMilliseconds2: milliseconds(10),
  durationList: [seconds(10)],
  macOS : isMacOS(),
  windows : isWindowsOS(),
  linux : isLinux(),
  unix : isUnix(),
  solaris : isSolaris()
};
