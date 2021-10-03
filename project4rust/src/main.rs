use std::convert::TryFrom;
use std::env::args;
use std::error::Error;
use std::fs::File;
use std::io::{BufRead, BufReader, ErrorKind};
use std::io::prelude::*;
use std::process::{Command, exit, id};
use std::time::Instant;

use crate::data_cube::DataCube;

mod shell_fragment;
mod data_cube;

struct CiiCube {
    main_cube: DataCube,
    lower_value: i32,
    // verbose: bool,
}

impl CiiCube {
    fn new() -> CiiCube {
        CiiCube { main_cube: DataCube::create(&vec![], 0), lower_value: 1, /*verbose: false*/ }
    }

    fn load(&mut self, filename: String) -> Result<(), Box<dyn Error>> {
        println!("Loading <{}>...", filename);

        let start_date = Instant::now();
        self.general_read_from_disk(&filename)?;
        let end_date = Instant::now();
        let num_seconds = (end_date - start_date).as_millis();
        println!("Milliseconds used to load the data\t{}", num_seconds);
        println!("Dimensions loaded\t{}", self.main_cube.get_number_shell_fragments());
        println!("Number of tuples loaded\t{}", self.main_cube.get_number_tuples());
        println!("Load ended");
        Ok(())
    }

    fn general_read_from_disk(&mut self, file_path: &String)
                              -> Result<(), Box<dyn std::error::Error>> {
        let path = File::open(file_path)?;

        let mut line = String::new();
        let mut reader = BufReader::new(path);
        let nbytes = reader.read_line(&mut line)?;
        if nbytes == 0 { return Err(thisbitchempty()?); }
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

            self.main_cube = DataCube::create(&sizes, self.lower_value);
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

            self.main_cube.add_tuple(i, &tuple);
        }
        self.main_cube.prone_data_cube();
        Ok(())
    }


    // let mut values: Vec<&str> = line.split(" ").collect();
    // remove_enters(&mut values);
    // values.pop();
    // totalTuple = values[0].parse().unwrap();
    //
    // sizes = vec!(0; values.len() - 1);
    // for i in 0..sizes.len() {
    //     sizes[i] = values[i + 1].parse::<i32>().unwrap();
    // }
//     }
//
//     let mut data_cube = DataCube::create( & sizes, lowerValues);
//     let numDimensions = sizes.len();
//     let mut newTuple = vec!(0; numDimensions);
//     for i in 0..totalTuple {
//     let nbytes = reader.read_line( & mut line) ?;
//     line = line.trim_end().to_string();
//     if nbytes == 0 {
//     return Err(thisbitchempty() ? );
//     }
//
//     let mut values: Vec < & str > = line.split( | c: char | c.is_whitespace()).collect();
//     remove_enters( & mut values);
//     if values.len() != numDimensions {
//     println ! ("tuple id = {} doesn't have the same number of dimensions", i);
//     exit(1);
//     }
//     for n in 0..numDimensions {
//     newTuple[n] = values[n].parse::< i32 > ().unwrap();
//     }
//     data_cube.add_tuple(i, & newTuple);
//     }
//
//     Ok(data_cube)
// }
}


fn main() -> Result<(), Box<dyn Error>> {
    let mut args = args();
    if args.len() <= 1 {
        println!("program <dataset>");
        exit(1);
    }
    println!("\nID REDUCTION MIXED ARRAY STYLE WITH CHANGED SUBCUBE!\n");
    let path = args.nth(1).expect("Did not had argument");
    println!("{}", path);
    let mut ciicube = CiiCube::new();
    ciicube.load(path)?;
    println!("pid: {}", id());
    let memoryusage = Command::new("bash")
        .args(&["-c", format!("pmap -p {} | tail -n 1 | cut -d ' ' -f 11-", id()).as_str()])
        .output()?.stdout;
    println!("Total memory used:\t");
    std::io::stdout().write_all(&*memoryusage)?;
    print!("bytes");
    Ok(())
}

fn thisbitchempty() -> Result<Box<dyn Error>, Box<dyn Error>> {
    Ok(Box::<std::io::Error>::try_from(std::io::Error::new(ErrorKind::InvalidInput, "This Bitch Empty YEET!!!!"))?)
}
