use std::env::args;
use std::fs::File;
use std::io::{BufRead, BufReader, ErrorKind, stdin, stdout, Write};
use std::process::{Command, exit, id};
use std::time::Instant;

use crate::data_cube::DataCube;

mod shell_fragment;
mod data_cube;

struct CiiCube {
    data_cube: DataCube,
    lower_value: i32,
    verbose: bool,
}

impl CiiCube {
    fn new() -> CiiCube {
        CiiCube { data_cube: DataCube::create(&vec![], 0), lower_value: 1, /*verbose: false*/ verbose: true }
    }

    fn load(&mut self, filename: String) -> Result<(), std::io::Error> {
        println!("Loading <{}>...", filename);

        let start_date = Instant::now();
        self.general_read_from_disk(&filename)?;
        let end_date = Instant::now();
        let num_seconds = (end_date - start_date).as_millis();
        println!("Milliseconds used to load the data\t{}", num_seconds);
        println!("Dimensions loaded\t{}", self.data_cube.get_number_shell_fragments());
        println!("Number of tuples loaded\t{}", self.data_cube.get_number_tuples());
        println!("Load ended");
        Ok(())
    }

    fn general_read_from_disk(&mut self, file_path: &String)
                              -> Result<(), std::io::Error> {
        let path = File::open(file_path)?;

        let mut line = String::new();
        let mut reader = BufReader::new(path);
        let nbytes = reader.read_line(&mut line)?;
        if nbytes == 0 { return Err(thisbitchempty()); }
        line = line.trim_end().to_string();

        let total_tuples;
        let num_dimensions;
        let mut tuple;
        {
            let values: Vec<&str> = line.split(" ").collect();
            total_tuples = values[0].parse().expect("Cant parse");
            let mut sizes = vec![0; values.len() - 1];//obtem o numero de tuplas
            for i in 1..values.len() {
                sizes[i - 1] = values[i].parse().expect("cant parse");
            }

            num_dimensions = values.len() - 1;
            tuple = vec![0; values.len() - 1];

            self.data_cube = DataCube::create(&sizes, self.lower_value);
        }

        for i in 0..total_tuples {
            line.clear();

            reader.read_line(&mut line)?;
            line = line.trim_end().to_string();
            let values: Vec<&str> = line.split(" ").collect();
            if values.len() != num_dimensions {
                panic!("tuple id = {} doesn't have the same number of dimensions, \nwith line content:\n\t{}\nand numdimensions: ", i, &line);
            }
            for n in 0..values.len() {
                tuple[n] = values[n].parse().expect("cant parse");
            }

            self.data_cube.add_tuple(i, &tuple);
        }
        self.data_cube.prone_data_cube();
        Ok(())
    }

    pub fn path_argument() -> Result<String, std::io::Error> {
        let mut args = args();
        if args.len() <= 1 {
            println!("program <dataset>");
            exit(1);
        }

        args.nth(1).ok_or_else(|| std::io::Error::new(ErrorKind::NotFound, "Argument not found"))
    }

    fn run(&mut self) -> Result<(), std::io::Error> {
        let pid = id();
        println!("\nID REDUCTION MIXED ARRAY STYLE WITH CHANGED SUBCUBE!\n");
        // let mut ciicube = CiiCube::new();
        self.load(CiiCube::path_argument()?)?;
        println!("PID: {}", pid);
        let memoryusage = get_memory(pid)?;
        println!("Total memory used: {}", memoryusage);
        loop {
            print!(">");
            stdout().flush();
            let mut input = String::new();
            let mut string_values = vec![];
            stdin().read_line(&mut input);
            input = input.to_lowercase().trim_end().to_string();
            string_values= input.split(" ").collect();  // faz split de cada uma das diemnsõesf
            if string_values.is_empty() { continue; }
            let first_value = string_values[0];
            if first_value == "q" {
                self.query(&string_values);
                println!("Used memory: {}", memoryusage);
            } else if first_value == "v" {
                self.change_verbose();
            } else if first_value == "sair" || first_value == "exit" || first_value == "quit" || first_value == "q" {
                break;
            } else {
                println!("Unknown command, use 'q' to query, 'v' to change verbosity and 'exit' to exit")
            }
        }

        Ok(())
    }

    /**
     * @param input user input. Something like "q 1 2 3"
     */
    fn query(&self, string_values: &Vec<&str>) {
        let mut values = vec![0; string_values.len() - 1];
        let mut sub_cube_flag = false;

        //coloca cada um dos valores no aray values
        for i in 1..string_values.len() {
            if let Ok(parsed) = string_values[i].parse() {
                values[i - 1] = parsed;
            } else {
                match string_values[i] {
                    "?" => {
                        values[i - 1] = -99;
                        sub_cube_flag = true;
                    }
                    "*" => {
                        values[i - 1] = -88;
                    }
                    _ => {
                        println!("Invalid value in query");
                        return;
                    }
                }
            }
        }
        let start_date = Instant::now();
        if sub_cube_flag {                  //caso seja um subcube
            self.data_cube.get_sub_cube(&values);
        } else {
            let search_result = self.data_cube.point_query_seach(&values); //returns array of ids
            if search_result.is_some() {
                println!("Query answers:\t{}", search_result.unwrap().len());
            } else {
                println!("Bad Query formation");
            }
        }
        println!("Query execute in {} ms.", (Instant::now() - start_date).as_millis());
    }

    fn change_verbose(&mut self) {
        self.verbose = !self.verbose;
        println!("verbose: {}", if self.verbose { "showing results" } else { "not showing results" })
    }
}

fn main() -> Result<(), std::io::Error> {
    let mut cii = CiiCube::new();
    cii.run()?;
    Ok(())
}

fn get_memory(pid: u32) -> Result<String, std::io::Error> {
    Ok(String::from_utf8_lossy(Command::new("bash")
        .args(&["-c", format!("pmap -p {} | tail -n 1 | cut -d ' ' -f 11-", pid).as_str()])
        .output()?.stdout.as_slice()).trim_start().trim_end().to_string())
}


fn thisbitchempty() -> std::io::Error {
    std::io::Error::new(ErrorKind::InvalidInput, "This Bitch Empty YEET!!!!")
}


