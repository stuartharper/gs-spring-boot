import ch.qos.logback.classic.encoder.PatternLayoutEncoder

appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%date %level %logger - %sanitise(%msg) %stack{10,128,1024,rootFirst}%n"
    }
}

root(ERROR, ['STDOUT'])
logger("io.lettuce", DEBUG)


