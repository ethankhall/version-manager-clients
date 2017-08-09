use slog::Logger;

use config::CromConfig;

pub fn execute_get(api_config: CromConfig, repo: String, id: String, logger: Logger) {
    let command_logger = logger.new(slog_o!());



}