const logger = require("log4js");
const configPath = require("./logConfig.json");

logger.configure(configPath);

const defaultLogger = logger.getLogger("default");
const systemLogger = logger.getLogger("system");
const httpLogger = logger.getLogger("http");
const accessLogger = logger.getLogger("access");

module.exports = logger;
module.exports.defaultLogger = defaultLogger;
module.exports.systemLogger = systemLogger;
module.exports.httpLogger = httpLogger;
module.exports.accessLogger = accessLogger;
