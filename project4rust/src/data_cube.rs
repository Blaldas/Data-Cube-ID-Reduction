use crate::shell_fragment_list::ShellFragment;

pub(crate) struct DataCube {
    shellFragmentList: Vec<ShellFragment>,
    lower: i32,
}

impl DataCube {
    /**
     * @param sizes      Max value of each dimension
     * @param lowerValue o menor valor do dataset (default = 1)
     *                   Chamada uma vez para criar o objeto cubo
     */
    pub fn create(sizes: &Vec<i32>, lowerValue: i32) -> DataCube {
        let mut dataCube = DataCube {
            shellFragmentList: Vec::with_capacity(sizes.len()),
            lower: lowerValue,
        };

        for size in sizes {
            dataCube.shellFragmentList.push(ShellFragment::create(lowerValue, *size));
        }
        dataCube
    }

    /**
     * @param arrayA
     * @param arrayB
     * @return chamar conuntos com o menor numero de tuples possivel
     */
    pub fn intersect(arrayA: &Vec<i32>, arrayB: &Vec<i32>) -> Vec<i32> {
        let mut c = vec!(0; arrayA.len().min(arrayB.len()));
        let mut ai = 0;
        let mut bi = 0;
        let mut ci = 0;

        while ai < arrayA.len() && bi < arrayB.len() {
            if arrayA[ai] == arrayB[bi] {
                if ci == 0 || arrayA[ai] != c[ci - 1] {
                    //if (arrayA[ai] != 0) {  Esta verificação foi removida porque os arrays enviados para aqui têm o tamanho estritamente necessário
//porém, esta linha estava a ignorar o tid 0 e a gastar tempo precioso
//Caso possa receber arrays com tamanho maior que o necessário, a linha pode ser usada como:
//if (arrayA[ai] != 0 && ai != 0)
                    c[ci] = arrayA[ai];
                    ci += 1;
//}
                }
                ai += 1;
                bi += 1;
            } else if arrayA[ai] > arrayB[bi] {
                bi += 1;
            } else if arrayA[ai] < arrayB[bi] {
                ai += 1;
            }
        }

        c.as_slice()[0usize..ci].into()
    }


    /**
     * @param tid    this tuple id
     * @param values Array of values to each dimension.
     */
    pub fn addTuple(&mut self, tid: i32, values: &Vec<i32>) {
        for shell in self.shellFragmentList.iter_mut().enumerate() {
            shell.1.addTuple(tid, values[shell.0]);
        }
    }

    /**
     * prints all the values and the number of tuples they store, for every single diemension
     */
    pub fn showAllDimensions(&self) {
        for (i, shell) in self.shellFragmentList.iter().enumerate() {
            println!("Dimension {}", i + 1);
            println!("Value\tNumberTuples");
            for n in shell.lower..shell.upper {
                println!("{}\t{:?}\n", n, shell.getTidsListFromValue(n));
            }
            println!("{}", shell.matrix[0].len());
        }
    }

    /**
     * @param query the query being made
     * @return array with the tuple ids, null if query.len() != shellFragmentList.len()
     */
    pub fn pointQuerySeach(&self, query: &Vec<i32>) -> Option<Vec<i32>> {
        if query.len() != self.shellFragmentList.len() {
            return None;
        }
        let mut instanciated = 0;
        let mut tidsList: Vec<Vec<i32>> = vec!(Vec::new(); self.shellFragmentList.len());         //stores values of instanciated
        for each in query.iter().enumerate() {                                   //obtem todas as listas de values
            if *each.1 != -88 && *each.1 != -99 {
                let returned: Vec<i32> = match self.shellFragmentList[each.0].getTidsListFromValue(*each.1) {
                    None => { return None; }
                    Some(result) => { result }
                };

                if returned.len() == 0 {                                      //se a lista for vazia, devolve lista com tamanho 0
                    return None;
                } else if instanciated == 0 {
                    tidsList[0] = returned.clone();      //obtem lista de tids
                } else {
                    for n in (0..=instanciated - 1).rev() {
                        if tidsList[n].len() > returned.len() {
                            tidsList[n + 1] = tidsList[n].clone();//TODO: rever clone
                            if n == 0 {
                                tidsList[0] = returned.clone();
                            }
                        } else {
                            tidsList[n + 1] = returned.clone();
                            break;
                        }
                    }
                }
                //tidsList[instanciated] = returned;
                instanciated += 1;
            }
        }

        let mut returnable = tidsList[0].clone();//TODO: rever clone
        if instanciated > 0 {
            for i in 1..instanciated {
                returnable = DataCube::intersect(&returnable, &tidsList[i]);
                if returnable.len() == 0 {
                    return Some(returnable);
                }
                return None;
            }
            return Some(returnable);
        }
        Some(self.shellFragmentList[0].getAllTids())
    }


    pub fn pointQuerySeachSubCube(&self, subCube: &Vec<ShellFragment>, query: &Vec<i32>) -> Option<Vec<i32>> {
        if query.len() != subCube.len() {
            return None;
        }

        let mut instanciated = 0;
        let mut tidsList = vec!(vec!(); subCube.len());                 //stores values of instanciated
        for each in query.iter().enumerate() {                                        //obtem todas as listas de values
            if *each.1 != -88 {
                let returned = subCube[each.0].getTidsListFromValueWithoutPronage(*each.1)?;
                if returned.len() == 0 {                                //se a lista for vazia, devolve lista com tamanho 0
                    return None;
                } else if instanciated == 0 {
                    tidsList[0] = returned;      //obtem lista de tids
                } else {
                    for n in instanciated - 1..=0 {
                        if tidsList[n].len() > returned.len() {
                            tidsList[n + 1] = tidsList[n].clone();//TODO: rever clone
                            if n == 0 {
                                tidsList[0] = returned.clone();//TODO: rever clone
                            }
                        } else {
                            tidsList[n + 1] = returned.clone();//TODO: rever clone
                            break;
                        }
                    }
                }
                //tidsList[instanciated] = returned;
                instanciated += 1;
            }
        }

        let mut d = tidsList[0].clone();
        if instanciated > 0 {
            for i in 1..instanciated {
                d = DataCube::intersect(&d, &tidsList[i]);
                if d.len() == 0 {
                    return Some(d.clone());
                }
            }
            return Some(d.clone());
        }
        return Some(subCube[0].getAllTids());
    }


    pub fn getNumberShellFragments(&self) -> i32
    {
        return self.shellFragmentList.len() as i32;
    }

    pub fn getNumberTuples(&self) -> i32
    {
        return self.shellFragmentList[0].getBiggestTid() + 1;
    }

    /**
     * @param values the query
     */
    pub fn getSubCube(&self, values: &Vec<i32>)
    {
        if values.len() != self.shellFragmentList.len() {
            println!("wrong number of dimensions");
            return;
        }

        let tidArray = match self.pointQuerySeach(values) {
            None => { return; }
            Some(x) => { x }
        };            //obtem TIDs resultante
        if tidArray.len() == 0 {
            println!("no values found");
            return;
        }

        //mostra resposta a query inicial:
        let mut str = String::new();
        for value in values {
            if *value == -99 || *value == -88 {
                str.push('*');
                str.push(' ');
            } else {
                str.push_str(&*value.to_string());
                str.push(' ');
            }
        }
        str.push_str(": ");
        str.push_str(&*tidArray.len().to_string());
        println!("{}", str);
        println!("A recriar sub dataset");
//para cada tid resultante
        let mut numInqiridas = 0;
        for i in values {
            if *i == -99 {
                numInqiridas += 1;
            }
        }
        let mut mapeamentoDimInq = vec!(0; numInqiridas);
        numInqiridas = 0;
        for (i, value) in values.iter().enumerate() {
            if *value == -99 {
                mapeamentoDimInq[numInqiridas] = i as i32;
                numInqiridas += 1;
            }
        }
        let mut subdataset = vec!(vec!(0i32; numInqiridas as usize); tidArray.len());//cada linha é uma tupla,

        // cada coluna é uma dimensão;

//para cada dimensão, obtem os tids, interceta com os tids do subCubo e adiciona os valores:
        for (d, map) in mapeamentoDimInq.iter().enumerate() {    //para cada uma das dimensões inquiridas
            for (i, _) in self.shellFragmentList[*map as usize].matrix.iter().enumerate() {      //para cada valor da dimensão
                let val = &self.shellFragmentList[*map as usize].matrix[i];                     //obtem lista de tids com esse valor
//note-se: val tem tamanho exato.
//faz interceção: val com tidArray
                let mut ti = 0;
                let mut vi = 0;
                while ti < tidArray.len() && vi < val.len() {   //interceção e adiciona
                    if tidArray[ti] == val[vi] {          //se igual
                        subdataset[ti][d] = (&i + self.lower as usize) as i32;    //lower igual a 1 para todas as diemnsões!
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

        // System.out.println(Arrays.deepToString(subdataset));
// System.exit(0);
        println!("A refazer cubo");
        let mut subCube = Vec::<ShellFragment>::with_capacity(numInqiridas);
        while subCube.len() < numInqiridas {
            subCube.push(ShellFragment::create(self.shellFragmentList[mapeamentoDimInq[subCube.len()] as usize].lower, self.shellFragmentList[mapeamentoDimInq[subCube.len()] as usize].upper));
        }

        for (i, _) in subdataset.iter().enumerate() {
            for d in 0..subCube.len() {
                subCube[d].addTuple(i as i32, subdataset[i][d]);
            }
        }

        println!("Subcubo acabado");
//System.out.println(Arrays.deepToString(subdataset));
        self.showQueryDataCube(&values, &mapeamentoDimInq, &subCube);
    }


    fn showQueryDataCube(&self, qValues: &Vec<i32>, mapeamentoDimInq: &Vec<i32>, subCube: &Vec<ShellFragment>)
    {
        let mut query = vec!(0; subCube.len());               //stores all the values as a query.
        let mut counter = vec!(0; subCube.len());             //counter to the query values

        let values = self.getAllDifferentValues(&qValues);      //guarda todos os valores diferentes para cada dimensão
        let mut total = 1;                              //guarda o numero de conbinações difrerentes
        for d in &values {
            total *= d.len();
        }
        let mut arrayQueriesEResultados = vec!(vec!(0;qValues.len()+1);total);
        while arrayQueriesEResultados.len() != total {
            arrayQueriesEResultados.push(Vec::with_capacity(qValues.len() + 1));
        }
        let mut rounds = 0;
        loop {
            for i in 0..counter.len() {     //da os valores as queries
                query[i] = values[mapeamentoDimInq[i] as usize][i].clone();
            }

            for (i, value) in qValues.iter().enumerate() {
                arrayQueriesEResultados[rounds][i] = *value;
            }

            for i in 0..mapeamentoDimInq.len() {
                arrayQueriesEResultados[rounds as usize][mapeamentoDimInq[i] as usize] = *&query[i];
            }

//pesquisa e mostra com valores do query
            arrayQueriesEResultados[rounds][qValues.len()] = *&self.pointQueryCounterSubCube(subCube, &query);// faz pesquisa sobre esses valores

//System.out.println(Arrays.toString(query) + " : " + arrayQueriesEResultados[rounds][qValues.len()]);


//gere os counters
            for (i, map) in mapeamentoDimInq.iter().enumerate() {             //para cada um dos counter
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
                for (int[] q : arrayQueriesEResultados) {
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
     * @param queryValues the query
     * @return a matrix with all the values to be looped, in each dimension.
     */
    fn getAllDifferentValues(&self, queryValues: &Vec<i32>) -> Vec<Vec<i32>>
    {
        let mut result = Vec::<Vec<i32>>::with_capacity(queryValues.len());
        for (i, queryvalue) in queryValues.iter().enumerate() {             //para cada uma das dimensões
            if *queryvalue == -99 {
                result[i] = vec![0; self.shellFragmentList[i].matrix.len()];
                result[i].push(-88);
                for mut j in (2..=result[i].len()).rev() {
                    result[i][j - 1] = j as i32;
                }
            } else {
                result[i][0] = queryValues[i];
            }
        }
        return result;
    }

    pub fn pointQueryCounterSubCube(&self, subCube: &Vec<ShellFragment>, query: &Vec<i32>) -> i32
    {
        let mat = self.pointQuerySeachSubCube(subCube, query);
        match mat {
            None => { -1 }
            Some(mat) => { mat.len() as i32 }
        }
    }


    pub fn getBiggestID(&self) -> i32
    {
        return self.shellFragmentList[0].getBiggestTid();
    }
    pub fn proneShellfragments(&self)
    {
        for s in &self.shellFragmentList {
            s.proneShellFragment();
        };
    }
}