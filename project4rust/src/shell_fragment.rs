pub struct ShellFragment {
    pub lower: i32,
    pub upper: i32,
    pub matrix: Vec<Vec<i32>>,
}

impl ShellFragment {
    pub fn create(lower: i32, upper: i32) -> ShellFragment {
        ShellFragment { lower, upper, matrix: vec!(vec!(0); (upper - lower + 1) as usize) }
    }

    pub fn add_tuple(&mut self, tid: i32, value: i32) {
        self.matrix[(value - self.lower) as usize].push(tid);
    }

    /**
     * @param value value of the dimension
     * @return ID list of the tuples with that value, if the value is not found returns an array with size 0.
     * Care that the array of a found value may be zero as well, so it's not a flag
     */
    pub fn get_tids_list_from_value(&self, value: i32) -> Option<Vec<i32>> {
        if value > self.upper || value < self.lower {
            None       //devolve nada
        } else {
            Some(self.matrix[(value - self.lower) as usize].clone())//se os valores nao estiverem nos intervalos
        }
    }

    pub fn get_tids_list_from_value_without_pronage(&self, value: i32) -> Option<Vec<i32>> {
        if value > self.upper || value < self.lower {
            None //devolve nada
        } else {
            Some(self.matrix[(value - self.lower) as usize].clone()) //se os valores nao estiverem nos intervalos
        }
    }

    pub fn get_bigest_value(&self) -> i32 {
        self.upper
    }

    /**
     * @return all the values being stored
     */
    pub fn get_all_values(&self) -> Vec<i32> {
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
     * <p>This function should actually be ignored, the method get_biggest_tid() should be used instead
     */
    pub fn get_all_tids(&self) -> Vec<i32> {
        let b = self.get_biggest_tid();                                //obtem o maior tid
        let mut returnable = vec!(0; (b + 1) as usize); //aloca array com o tamanho do maior tid+1

        for each in returnable.iter_mut().enumerate() {
            *each.1 = each.0 as i32;                                  //coloca o seu index
        }
        returnable
    }


    /**
     * @return the biggest tid stored int the shellfragment
     */
    pub fn get_biggest_tid(&self) -> i32
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
    pub fn prone_shell_fragment(&self){
        let mut b;
        for (v, elem) in self.matrix.iter().enumerate(){
            b = vec!(0;elem.len());
            for i in (1..elem.len()).rev(){
                b[i] = self.matrix[v][i];
            }
        }
    }
}
