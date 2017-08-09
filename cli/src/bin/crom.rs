#[macro_use]
extern crate clap;
extern crate crom;
#[macro_use]
extern crate slog;
extern crate slog_term;
extern crate slog_async;
extern crate yaml_rust;

use clap::App;
use std::fs::File;
use std::io::prelude::*;
use slog::{Level, Drain, Logger};
use crom::get_command::execute_get;
use crom::config::CromConfig;
use yaml_rust::YamlLoader;

pub fn logging(min_level: Level) -> Logger {
    let decorator = slog_term::TermDecorator::new().build();
    let drain = slog_term::FullFormat::new(decorator).build().fuse();
    let drain = drain.filter_level(min_level).fuse();
    let drain = slog_async::Async::new(drain).build().fuse();
    return Logger::root(drain, slog_o!("version" => env!("CARGO_PKG_VERSION")));
}

pub fn log_level(quite_set: bool, number_of_verbose: u64) -> Level {
    if quite_set {
        return Level::Warning;
    }
    return match number_of_verbose {
        0 => Level::Info,
        1 => Level::Debug,
        2 | _ => Level::Trace,
    };
}

fn default_crom_config() -> CromConfig {
    return CromConfig { api_server: "http://api.crom.tech".to_string(), api_token: "".to_string()}
}

fn build_crom_config(logger: Logger, file_path: &str) -> CromConfig {
    debug!(logger, "Value for config: {}", file_path);

    let config = default_crom_config();

    let mut f = File::open(file_path).expect("file not found");

    let mut contents = String::new();
    f.read_to_string(&mut contents);

    let docs = YamlLoader::load_from_str(contents.as_str()).unwrap();
    let doc = &docs[0];
    doc.h
    let apiServer = doc["crom"]["api-server"].as_str().unwrap();

    return config;
}

fn main() {
    let yaml = load_yaml!("cli.yml");
    let matches = App::from_yaml(yaml).get_matches();
    let level = log_level(matches.is_present("quite"), matches.occurrences_of("verbose"));

    let logger = logging(level);
    debug!(logger, "Logging at {} ready!", level);

    // Gets a value for config if supplied by user, or defaults to "default.conf"
    let config = match matches.value_of("config") {
        Some(value) => build_crom_config(logger, value),
        _ => default_crom_config()
    };


    match matches.subcommand() {
        ("get", Some(get_matches)) => {
            execute_get(get_matches.value_of("NAME").unwrap().to_string(),
                        get_matches.value_of("ID").unwrap().to_string(),
                        logger);
        },
        ("",  None)        => println!("No subcommand was used"),
        _           => unreachable!(), // Assuming you've listed all direct children above, this is unreachable
    }
    //    logger.filter_level()
}
