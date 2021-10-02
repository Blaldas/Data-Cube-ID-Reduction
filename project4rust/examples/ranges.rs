use std::process::{Command, id};
use std::io::Read;
use std::error::Error;

fn main() -> Result<(), Box<dyn Error>>{
    let mut memoryusage= "".to_string();
    let size = Command::new("bash")
        .args(&["-c", "echo boas"])
        .output()?.stdout;
    println!("Total memory used:\t {} bytes", memoryusage);
    Ok(())
}