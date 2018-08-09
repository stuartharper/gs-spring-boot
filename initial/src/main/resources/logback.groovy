import ch.qos.logback.classic.encoder.PatternLayoutEncoder

appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%date %level %logger - %msg%n"
    }
}

root(ERROR, ['STDOUT'])
logger("io.lettuce", DEBUG)


