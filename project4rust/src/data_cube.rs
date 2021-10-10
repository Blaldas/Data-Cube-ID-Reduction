use std::ops::Add;

use crate::shell_fragment::ShellFragment;

pub(crate) struct DataCube {
    shell_fragment_list: Vec<ShellFragment>,
    lower: i32,
}

impl DataCube {
    /**
     * @param sizes      Max value of each dimension
     * @param lower_value o menor valor do dataset (default = 1)
     *                   Chamada uma vez para criar o objeto cubo
     */
    pub fn create(sizes: &Vec<i32>, lower_value: i32) -> DataCube {
        let mut data_cube = DataCube {
            shell_fragment_list: Vec::with_capacity(sizes.len()),
            lower: lower_value,
        };

        for size in sizes {
            data_cube.shell_fragment_list.push(ShellFragment::create(lower_value, *size));
        }
        data_cube
    }

    /**
     * @param array_a
     * @param array_b
     * @return chamar conuntos com o menor numero de tuples possivel
     */
    pub fn intersect(array_a: &Vec<i32>, array_b: &Vec<i32>) -> Vec<i32> {
        let mut c = vec!(0; array_a.len().min(array_b.len()));
        let mut ai = 0;
        let mut bi = 0;
        let mut ci = 0;

        while ai < array_a.len() && bi < array_b.len() {
            if array_a[ai] == array_b[bi] {
                if ci == 0 || array_a[ai] != c[ci - 1] {
                    c[ci] = array_a[ai];
                    ci += 1;
                }
                ai += 1;
                bi += 1;
            } else if array_a[ai] > array_b[bi] {
                bi += 1;
            } else if array_a[ai] < array_b[bi] {
                ai += 1;
            }
        }

        c.as_slice()[0usize..ci].into()
    }

    /**
     * @param tid    this tuple id
     * @param values Array of values to each dimension.
     */
    pub fn add_tuple(&mut self, tid: i32, values: &Vec<i32>) {
        for shell in self.shell_fragment_list.iter_mut().enumerate() {
            shell.1.add_tuple(tid, values[shell.0]);
        }
    }

    /**
     * prints all the values and the number of tuples they store, for every single diemension
     */
    pub fn show_all_dimensions(&self) {
        for (i, shell) in self.shell_fragment_list.iter().enumerate() {
            println!("Dimension {}", i + 1);
            println!("Value\tNumberTuples");
            for n in shell.lower..shell.upper {
                println!("{}\t{:?}\n", n, shell.get_tids_list_from_value(n));
            }
            println!("{}", shell.matrix[0].len());
        }
    }

    /**
     * @param query the query being made
     * @return array with the tuple ids, null if query.len() != shell_fragment_list.len()
     */
    pub fn point_query_seach(&self, query: &Vec<i32>) -> Option<Vec<i32>> {
        if query.len() != self.shell_fragment_list.len() {
            return None;
        }
        let mut instanciated = 0;
        let mut tids_list: Vec<Vec<i32>> = vec!(Vec::new(); self.shell_fragment_list.len());         //stores values of instanciated
        for each in query.iter().enumerate() {                                   //obtem todas as listas de values
            if *each.1 != -88 && *each.1 != -99 {
                let returned: Vec<i32> = match self.shell_fragment_list[each.0].get_tids_list_from_value(*each.1) {
                    None => {
                        return None;
                    }
                    Some(result) => { result }
                };

                if returned.len() == 0 {                                      //se a lista for vazia, devolve lista com tamanho 0
                    return None;
                } else if instanciated == 0 {
                    tids_list[0] = returned.clone();      //obtem lista de tids
                } else {
                    for n in (0..=instanciated - 1).rev() {
                        if tids_list[n].len() > returned.len() {
                            tids_list[n + 1] = tids_list[n].clone();//TODO: rever clone
                            if n == 0 {
                                tids_list[0] = returned.clone();
                            }
                        } else {
                            tids_list[n + 1] = returned.clone();
                            break;
                        }
                    }
                }
                //tids_list[instanciated] = returned;
                instanciated += 1;
            }
        }

        let mut returnable = tids_list[0].clone();//TODO: rever clone
        if instanciated > 0 {
            for i in 1..instanciated {
                returnable = DataCube::intersect(&returnable, &tids_list[i]);
                if returnable.len() == 0 {
                    return Some(returnable);
                }
            }
            return Some(returnable);
        }
        Some(self.shell_fragment_list[0].get_all_tids())
    }


    pub fn point_query_seach_sub_cube(&self, sub_cube: &Vec<ShellFragment>, query: &Vec<i32>) -> Option<Vec<i32>> {
        if query.len() != sub_cube.len() {
            return None;
        }

        let mut instanciated = 0;
        let mut tids_list = vec!(vec!(); sub_cube.len());                 //stores values of instanciated
        for each in query.iter().enumerate() {                                        //obtem todas as listas de values
            if *each.1 != -88 {
                let returned = sub_cube[each.0].get_tids_list_from_value_without_pronage(*each.1)?;
                if returned.len() == 0 {                                //se a lista for vazia, devolve lista com tamanho 0
                    return None;
                } else if instanciated == 0 {
                    tids_list[0] = returned;      //obtem lista de tids
                } else {
                    for n in instanciated - 1..=0 {
                        if tids_list[n].len() > returned.len() {
                            tids_list[n + 1] = tids_list[n].clone();//TODO: rever clone
                            if n == 0 {
                                tids_list[0] = returned.clone();//TODO: rever clone
                            }
                        } else {
                            tids_list[n + 1] = returned.clone();//TODO: rever clone
                            break;
                        }
                    }
                }
                //tids_list[instanciated] = returned;
                instanciated += 1;
            }
        }

        let mut d = tids_list[0].clone();
        if instanciated > 0 {
            for i in 1..instanciated {
                d = DataCube::intersect(&d, &tids_list[i]);
                if d.len() == 0 {
                    return Some(d.clone());
                }
            }
            return Some(d.clone());
        }
        return Some(sub_cube[0].get_all_tids());
    }


    pub fn get_number_shell_fragments(&self) -> i32
    {
        return self.shell_fragment_list.len() as i32;
    }

    pub fn get_number_tuples(&self) -> i32
    {
        return self.shell_fragment_list[0].get_biggest_tid() + 1;
    }

//     /**
//      * @param values the query
//      */
//     pub fn get_sub_cube(&self, values: &Vec<i32>)
//     {
//         if values.len() != self.shell_fragment_list.len() {
//             println!("wrong number of dimensions");
//             return;
//         }
//
//         let tid_array = match self.point_query_seach(values) {
//             None => { return; }
//             Some(x) => { x }
//         };            //obtem TIDs resultante
//         if tid_array.len() == 0 {
//             println!("no values found");
//             return;
//         }
//
//         //mostra resposta a query inicial:
//         let mut str = String::new();
//         for value in values {
//             if *value == -99 || *value == -88 {
//                 str.push('*');
//                 str.push(' ');
//             } else {
//                 str.push_str(&*value.to_string());
//                 str.push(' ');
//             }
//         }
//         str.push_str(": ");
//         str.push_str(&*tid_array.len().to_string());
//         println!("{}", str);
//         println!("A recriar sub dataset");
// //para cada tid resultante
//         let mut num_inqiridas = 0;
//         for i in values {
//             if *i == -99 {
//                 num_inqiridas += 1;
//             }
//         }
//         let mut mapeamento_dim_inq = vec!(0; num_inqiridas);
//         num_inqiridas = 0;
//         for (i, value) in values.iter().enumerate() {
//             if *value == -99 {
//                 mapeamento_dim_inq[num_inqiridas] = i as i32;
//                 num_inqiridas += 1;
//             }
//         }
//         let mut subdataset = vec!(vec!(0i32; num_inqiridas as usize); tid_array.len());//cada linha é uma tupla,
//
//         // cada coluna é uma dimensão;
//
// //para cada dimensão, obtem os tids, interceta com os tids do subCubo e adiciona os valores:
//         for (d, map) in mapeamento_dim_inq.iter().enumerate() {    //para cada uma das dimensões inquiridas
//             for (i, _) in self.shell_fragment_list[*map as usize].matrix.iter().enumerate() {      //para cada valor da dimensão
//                 let val = &self.shell_fragment_list[*map as usize].matrix[i];                     //obtem lista de tids com esse valor
// //note-se: val tem tamanho exato.
// //faz interceção: val com tid_array
//                 let mut ti = 0;
//                 let mut vi = 0;
//                 while ti < tid_array.len() && vi < val.len() {   //interceção e adiciona
//                     if tid_array[ti] == val[vi] {          //se igual
//                         subdataset[ti][d] = (&i + self.lower as usize) as i32;    //lower igual a 1 para todas as diemnsões!
//                         ti += 1;
//                         vi += 1;
//                     } else if tid_array[ti] < val[vi] {
//                         ti += 1;
//                     } else {
//                         vi += 1;
//                     }
//                 }
//             }
//         }
//
//         // System.out.println(Arrays.deepToString(subdataset));
// // System.exit(0);
//         println!("A refazer cubo");
//         let mut sub_cube = Vec::<ShellFragment>::with_capacity(num_inqiridas);
//         while sub_cube.len() < num_inqiridas {
//             sub_cube.push(ShellFragment::create(self.shell_fragment_list[mapeamento_dim_inq[sub_cube.len()] as usize].lower, self.shell_fragment_list[mapeamento_dim_inq[sub_cube.len()] as usize].upper));
//         }
//
//         for (i, _) in subdataset.iter().enumerate() {
//             for d in 0..sub_cube.len() {
//                 sub_cube[d].add_tuple(i as i32, subdataset[i][d]);
//             }
//         }
//
//         println!("Subcubo acabado");
// //System.out.println(Arrays.deepToString(subdataset));
//         self.show_query_data_cube(&values, &mapeamento_dim_inq, &sub_cube);
//     }


    fn show_query_data_cube(&self, q_values: &Vec<i32>, mapeamento_dim_inq: &Vec<i32>, sub_cube: &Vec<ShellFragment>)
    {
        let mut query = vec!(0; sub_cube.len());               //stores all the values as a query.
        let mut counter = vec!(0; sub_cube.len());             //counter to the query values

        let values = self.get_all_different_values(&q_values);      //guarda todos os valores diferentes para cada dimensão
        let mut total = 1;                              //guarda o numero de conbinações difrerentes
        for d in &values {
            total *= d.len();
        }
        let mut array_queries_eresultados = vec!(vec!(0; q_values.len() + 1); total);
        while array_queries_eresultados.len() != total {
            array_queries_eresultados.push(Vec::with_capacity(q_values.len() + 1));
        }
        let mut rounds = 0;
        loop {
            for i in 0..counter.len() {     //da os valores as queries
                query[i] = values[mapeamento_dim_inq[i] as usize][i].clone();
            }

            for (i, value) in q_values.iter().enumerate() {
                array_queries_eresultados[rounds][i] = *value;
            }

            for i in 0..mapeamento_dim_inq.len() {
                array_queries_eresultados[rounds as usize][mapeamento_dim_inq[i] as usize] = *&query[i];
            }

//pesquisa e mostra com valores do query
            array_queries_eresultados[rounds][q_values.len()] = *&self.point_query_counter_sub_cube(sub_cube, &query);// faz pesquisa sobre esses valores

//System.out.println(Arrays.toString(query) + " : " + array_queries_eresultados[rounds][q_values.len()]);


//gere os counters
            for (i, map) in mapeamento_dim_inq.iter().enumerate() {             //para cada um dos counter
                if counter[i] < values[*map as usize].len() - 1 {
                    counter[i] += 1;
                    break;
                } else {        //if( counter[i] == '*')
                    counter[i] = 0;
                }
            }
            rounds += 1;
            if rounds < total { break; }
        }

        /*        StringBuilder str = new StringBuilder();
                for (int[] q : array_queries_eresultados) {
                    str.setLength(0);
                    for (int i = 0; i < q.len() - 1; i++) {
                        if (q[i] == -88)
                            str.append('*').append(" ");
                        else if (q[i] == -99)
                            str.append('?').append(" ");
                        else
                            str.append(q[i]).append(" ");
                    }
                    str.append(" : ").append(q[q.len() - 1]);
                    System.out.println(str);
                }
         */


        println!("{} lines written", total);
    }

    /**
     * @param query_values the query
     * @return a matrix with all the values to be looped, in each dimension.
     */
    fn get_all_different_values(&self, query_values: &Vec<i32>) -> Vec<Vec<i32>>
    {
        let mut result = Vec::<Vec<i32>>::with_capacity(query_values.len());
        for (i, queryvalue) in query_values.iter().enumerate() {             //para cada uma das dimensões
            if *queryvalue == -99 {
                result[i] = vec![0; self.shell_fragment_list[i].matrix.len()];
                result[i].push(-88);
                for j in (2..=result[i].len()).rev() {
                    result[i][j - 1] = j as i32;
                }
            } else {
                result[i][0] = query_values[i];
            }
        }
        return result;
    }

    pub fn point_query_counter_sub_cube(&self, sub_cube: &Vec<ShellFragment>, query: &Vec<i32>) -> i32
    {
        let mat = self.point_query_seach_sub_cube(sub_cube, query);
        match mat {
            None => { -1 }
            Some(mat) => { mat.len() as i32 }
        }
    }

    pub fn get_sub_cube(&self, values: &Vec<i32>) -> Result<(), ()> {
        if values.len() != self.shell_fragment_list.len() {
            println!("Wrong number of dimensions");
            return Err(());
        }

        if let Some(tidArray) = self.point_query_seach(&values) {
            let mut string = String::new();
            for i in 0..values.len() {
                if values[i] == -99 || values[i] == -88 {
                    string += "* ";
                } else {
                    string += &values[i].to_string();
                    string += " ";
                }
            }
            string += ": ";
            string += &tidArray.len().to_string();
            println!("{}", string);

            println!("A recriar sub dataset");
            //para cada tid resultant
            let mut num_inqiridas = 0;
            for i in values {
                if *i == -99 as i32 {
                    num_inqiridas += 1;
                }
            }
            let mut mapeamento_dim_inq = vec![0 as i32; num_inqiridas];
            num_inqiridas = 0;
            for i in 0..values.len() {
                if values[i] == -99 {
                    mapeamento_dim_inq[num_inqiridas] = i as i32;
                    num_inqiridas += 1;
                }
            }

            //cada linha é uma tupla, cada coluna é uma dimensão
            let mut subdataset = vec![vec![0; num_inqiridas]; tidArray.len()];

            //para cada dimensão, obtem os tids, interceta com os tids do subCubo e adiciona os valores:
            for d in 0..mapeamento_dim_inq.len() {
                for i in 0..self.shell_fragment_list[mapeamento_dim_inq[d] as usize].matrix.len() {
                    let val = &self.shell_fragment_list[mapeamento_dim_inq[d] as usize].matrix[i];
                    //nota-se: val tem tamanho exato.
                    //faz interceção val com tidArray
                    let mut ti = 0;
                    let mut vi = 0;
                    while ti < tidArray.len() && vi < val.len() {
                        if tidArray[ti] == val[vi] {
                            subdataset[ti][d] = i + self.lower as usize;
                            ti += 1;
                            vi += 1;
                        } else if tidArray[ti] < val[vi] {
                            ti += 1;
                        } else {
                            vi += 1;
                        }
                    }
                }
            }

            println!("A refazer cubo");
            let mut subCube = vec![ShellFragment {
                lower: 0,
                upper: 0,
                matrix: vec![],
            }; num_inqiridas];
            for i in 0..subCube.len() {
                subCube[i] = ShellFragment::create(self.shell_fragment_list[mapeamento_dim_inq[i] as usize].lower,
                                                   self.shell_fragment_list[mapeamento_dim_inq[i] as usize].upper);
            }
            for i in 0..subdataset.len() {
                for d in 0..subCube.len() {
                    subCube[d].add_tuple(i as i32, subdataset[i][d] as i32);
                }
            }

            for i in 0..subCube.len() {
                subCube[i].prone_shell_fragment();
            }

            println!("Subcubo acabado");

            self.show_query_data_cube(&values, &mapeamento_dim_inq, &subCube);
            Ok(())
        } else {
            println!("No values found.");
            Err(())
        }
    }


    // pub fn get_biggest_id(&self) -> i32
    // {
    //     return self.shell_fragment_list[0].get_biggest_tid();
    // }

    // pub fn prone_shellfragments(&mut self)
    // {
    //     for s in &self.shell_fragment_list {
    //         s.prone_shell_fragment();
    //     };
    // }

    pub fn prone_data_cube(&mut self) {
        for s in &self.shell_fragment_list {
            s.prone_shell_fragment();
        }
    }
}