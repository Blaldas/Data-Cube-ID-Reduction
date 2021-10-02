use std::convert::TryFrom;
use std::env::args;
use std::error::Error;
use std::fs::File;
use std::io::{BufRead, BufReader, ErrorKind, Stdin, Stdout};
use std::io::prelude::*;
use std::num::ParseIntError;
use std::process::{Command, exit, id};
use std::time::Instant;

use crate::data_cube::DataCube;

mod shell_fragment_list;
mod data_cube;

struct CiiCube {
    mainCube: DataCube,
    lowerValue: i32,
    verbose: bool,
}

impl CiiCube {
    fn new() -> CiiCube {
        CiiCube { mainCube: DataCube::create(&vec![], 0), lowerValue: 1, verbose: false }
    }

    fn load(self: Self, filename: String) -> Result<DataCube, Box<dyn Error>> {
        println!("Loading <{}>...", filename);
        let startDate = Instant::now();
        let mainCube = generalReadFromDisk(&filename)?;
        let endDate = Instant::now();

        let numSeconds = (endDate - startDate).as_millis();
        println!("Milliseconds used to load the data\t{}", numSeconds);
        println!("Dimensions loaded\t{}", mainCube.getNumberShellFragments());
        println!("Number of tuples loaded\t{}", mainCube.getNumberTuples());
        println!("Load ended");
        Ok(mainCube)
    }

    fn generalReadFromDisk(filePath: &String)
                           -> Result<DataCube, Box<dyn std::error::Error>> {
        let mut path = File::open(filePath)?;

        let mut line = String::new();
        let totalTuples;

        let mut reader = BufReader::new(path);

        let nbytes = reader.read_line(&mut line)?;
        if nbytes == 0 { return Err(thisbitchempty()?); }
        line = line.trim_start().trim_end().to_string();
        let values: Vec<&str> = line.split(" ").collect();

        let totalTuple: i32 = values[0].parse().expect("Cant parse");
        let mut sizes = values.len() - 1;//obtem o numero de tuplas
        for i in 1..values.len() {
            sizes[i - 1] = values[i].parse().expect("cant parse");
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
    }

    let mut data_cube = DataCube::create( & sizes, lowerValues);
    let numDimensions = sizes.len();
    let mut newTuple = vec!(0; numDimensions);
    for i in 0..totalTuple {
    let nbytes = reader.read_line( & mut line) ?;
    line = line.trim_end().to_string();
    if nbytes == 0 {
    return Err(thisbitchempty() ? );
    }

    let mut values: Vec < & str > = line.split( | c: char | c.is_whitespace()).collect();
    remove_enters( & mut values);
    if values.len() != numDimensions {
    println ! ("tuple id = {} doesn't have the same number of dimensions", i);
    exit(1);
    }
    for n in 0..numDimensions {
    newTuple[n] = values[n].parse::< i32 > ().unwrap();
    }
    data_cube.addTuple(i, & newTuple);
    }

    Ok(data_cube)
}
}


fn main() {
    let mut args = args();

    if args.len() <= 1 {
        println!("program <dataset>");
        exit(1);
    }
    println!("\nID REDUCTION MIXED ARRAY STYLE WITH CHANGED SUBCUBE!\n");

    let path = args.nth(2).expect("Did not had element");
    println!("{}", path);

    let ciicube = CiiCube::new();
    ciicube.load();


    // let path = args.nth(1).expect("fragCubing_java.jar <dataset name>");
    // println!("argumento: {}", path);
    // let mainCube = load(path)?;
    // println!("{}", id());
    // let memoryusage = Command::new("bash")
    //     .args(&["-c", format!("pmap -p {} | tail -n 1 | cut -d ' ' -f 11-", id()).as_str()])
    //     .output()?.stdout;
    //
    // println!("Total memory used:\t");
    // std::io::stdout().write_all(&*memoryusage);
    // print!("bytes");
}

//
//
//
// fn old() -> Result<(), Box<dyn Error>> {
//     println!("\nnot using ID REDUCTION \n");
//     let mut args = args();
//
//     let path = args.nth(1).expect("fragCubing_java.jar <dataset name>");
//     println!("argumento: {}", path);
//     let mainCube = load(path)?;
//     println!("{}", id());
//     let memoryusage = Command::new("bash")
//         .args(&["-c", format!("pmap -p {} | tail -n 1 | cut -d ' ' -f 11-", id()).as_str()])
//         .output()?.stdout;
//
//     println!("Total memory used:\t");
//     std::io::stdout().write_all(&*memoryusage);
//     print!("bytes");
//
//     Ok(())
// }
//

//
// fn thisbitchempty() -> Result<Box<dyn Error>, Box<dyn Error>> {
//     Ok(Box::<std::io::Error>::try_from(std::io::Error::new(ErrorKind::InvalidInput, "This Bitch Empty YEET!!!!"))?)
// }
//
// fn remove_enters(values: &mut Vec<&str>) {
//     if values.last().is_some() {
//         while let Some(index) = values.last().unwrap().find(|c: char| c.is_whitespace()) {
//             values.pop();
//             println!("found it");
//         }
//     }
// }
//
// fn read_until_0xA() -> String {
//     let c = &mut [1];
//     let mut buffer = vec!(0u8, 0);
//     while let Ok(size) = file.read(c) > 0 {
//         if c != 0xA {
//             buffer.push(c[0]);
//             line.push(char::from());
//         } else {
//             break;
//         }
//     }
//     String::from_utf8(buffer).unwrap_or_else(String::new())
// }
//
//