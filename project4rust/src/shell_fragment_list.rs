// pub struct DIntArray {
//     reduced_pos1: Vec<i32>,
//     reduced_pos2:Vec<i32>,
//     no_reduction_array: Vec<i32>,
//     size_reduced: usize,
//     size_non_reduced: usize,
// }
//
// impl DIntArray{
//     pub fn create() -> DIntArray{
//         DIntArray{ reduced_pos1: vec!(), reduced_pos2: vec!(), no_reduction_array: vec!(), size_reduced: 0, size_non_reduced: 0 }
//     }
//     pub fn reduce_maximum_memory(&mut self){
//         self.reduced_pos1 = self.reduced_pos1.as_slice()[0..self.size_reduced].into();
//         self.reduced_pos2 = self.reduced_pos2.as_slice()[0..self.size_reduced].into();
//         self.no_reduction_array = self.no_reduction_array.as_slice()[0..self.size_non_reduced].into();
//     }
//
//     pub fn add_tid(new_tid: i32){
//         //Acrescenta-se à compressão
//         if sizeReduced > 0 && reducedPos2[sizeReduced - 1] + 1 == newTid {
//             reducedPos2[sizeReduced - 1] = newTid;
//         }
//
//         //nova compressão -> 3 elementos seguidos com tids seguidos
//         else if sizeNonReduced >= 2 && noReductionArray[sizeNonReduced - 1] + 1 == newTid && noReductionArray[sizeNonReduced - 2] + 2 == newTid {
//             //se nao tiver mais espaço realoca
//             if sizeReduced == reducedPos1.len(){
//                 increaseReducedArrays();
//             }
//             //coloca valores e aumenta ponteiro
//             reducedPos1[sizeReduced] = noReductionArray[sizeNonReduced - 2];
//             reducedPos2[sizeReduced+=1] = newTid;
//             //remove valores do array sem redução
//             // usar de houver problemas....:
//             // noReductionArray[sizeNonReduced-1] = 0;
//             // noReductionArray[sizeNonReduced-2] = 0;
//             sizeNonReduced -= 2;
//         }
//         //não acrescenta a redução existente nem cria novo redução:
//         else {
//             //se nao tiver mais espaço realoca
//             if (sizeNonReduced == noReductionArray.len())
//             increaseNonReducedArray();
//             //adiciona novo valor
//             noReductionArray[sizeNonReduced+=1] = newTid;
//         }
//
//
//
//     }
//
//     pub fn add_tid_interval(v1 : i32, v2 : i32) {
//         if sizeReduced == reduced_pos1.len(){
//             increaseReducedArrays();
//         }
//         reduced_pos1[size_reduced] = v1;
//         reduced_pos2[size_reduced+=1] = v2;
//
//     }
//     pub fn increaseReducedArrays() {
//     int[] a = new int[reducedPos1.len() == 0 ? 1 : 2 * reducedPos1.len()];
//     int[] b = new int[reducedPos1.len() == 0 ? 1 : 2 * reducedPos1.len()];
//
//     for (int i = 0; i < sizeReduced; i++) {
//     a[i] = reducedPos1[i];
//     b[i] = reducedPos2[i];
//     }
//
//     reducedPos1 = a;
//     reducedPos2 = b;
//     }
//
// }

pub struct ShellFragment {
    pub lower: i32,
    pub upper: i32,
    pub matrix: Vec<Vec<i32>>,
}

impl ShellFragment {
    pub fn create(lower: i32, upper: i32) -> ShellFragment {
        ShellFragment { lower, upper, matrix: vec!(vec!(0); (upper - lower + 1) as usize) }
    }

    pub fn addTuple(&mut self, tid: i32, value: i32) {
        self.matrix[(value - self.lower) as usize].push(tid);
    }

    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0.
     * Care that the array of a found value may be zero as well, so it's not a flag
     */
    pub fn getTidsListFromValue(&self, value: i32) -> Option<Vec<i32>> {
        if value > self.upper || value < self.lower {
            None       //devolve nada
        } else {
            Some(self.matrix[(value - self.lower) as usize].clone())//se os valores nao estiverem nos intervalos
        }
    }

    pub fn getTidsListFromValueWithoutPronage(&self, value: i32) -> Option<Vec<i32>> {
        if value > self.upper || value < self.lower {
            None //devolve nada
        } else {
            Some(self.matrix[(value - self.lower) as usize].clone()) //se os valores nao estiverem nos intervalos
        }
    }

    // /**
    //  * @param tid id of the tuple to be seached
    //  * @return the value of such tuple, or lower-1 if not found.
    //  */
    // pub fn getValueFromTid(&self, tid : i32) -> i32 {
    // for (int i = 0; i < matrix.len(); i + + ) {                                     //para cada um dos valores (arrays de tids)
    // int pos = IntArrays.binarySearch(matrix[i], 0, size[i], tid);                 //faz pesquisa binária
    // if (pos > = 0)                                                //se a pesquisa binária der resultado positivo (o resultado é a posição)
    // return lower + i;                                                  //devole valor da posição
    // }
    // return lower - 1;                               //devove valor menor que o minimo
    // }


    pub fn getBigestValue(&self) -> i32 {
        self.upper
    }

    /**
     * @return all the values being stored
     */
    pub fn getAllValues(&self) -> Vec<i32> {
        let mut returnable = vec!(0; self.matrix.len());
        let mut x = self.lower;
        for i in 0..returnable.len() {
            returnable[i] = x;
            x += 1;
        }//devolve array com valores
        returnable
    }


    /**
     * @return returns an array with all the tids
     * <p>This function should actually be ignored, the method getBiggestTid() should be used instead
     */
    pub fn getAllTids(&self) -> Vec<i32> {
        let b = self.getBiggestTid();                                //obtem o maior tid
        let mut returnable = vec!(0; (b + 1) as usize); //aloca array com o tamanho do maior tid+1

        for mut each in returnable.iter_mut().enumerate() {
            *each.1 = each.0 as i32;                                  //coloca o seu index
        }
        returnable
    }


    /**
     * @return the biggest tid stored int the shellfragment
     */
    pub fn getBiggestTid(&self) -> i32
    {
        let mut max = -1;
        for elem in &self.matrix {
            if let Some(x) = elem.last(){
                if max < *x {
                    max = *x;
                }
            }
        }
        return max;                                             //returns the biggest value
    }
    pub fn proneShellFragment(&self){
        let mut b;
        for (v, elem) in self.matrix.iter().enumerate(){
            b = vec!(0;elem.len());
            for mut i in (1..=elem.len()).rev(){
                b[i-1] = self.matrix[v][i];
            }
        }
    }
}
