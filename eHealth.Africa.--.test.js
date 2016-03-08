
//DEPTH FIRST TREE TRAVERSAL
//Discuss an algorithm to traverse a tree, depth first.
//
//ANSWER:
//The depth-first search is an algorithm for traversing or searching tree or graph data structures. Starting from the root and exploring as far as 
//possible along each branch before backtracking. There are three different kinds of depth first traversals:
//Pre-order: Where it visits each node before visiting any of its children
//In-order: Where it visits each node after visiting its left child, but before visiting its right child
//Post-order: Where it visit each node after visiting both of its children


//CHARACTERS IN STRINGS

//Implement a function with signature find_chars(string1, string2) that takes two strings and returns a string that 
//contains only the characters found in string1 and string two in the order that they are found in string1. Implement 
//a version of order N*N and one of order N.

function find_char_nn(string1, string2){
	result  = "";
	for(i = 0; i<string1.length; i++){

		for(j = 0; j<string2.length; j++){
			if (string2[j] == string1[i]){
				result  += string2[j];
			}
		}

	}
	return result;
}


function find_char_n(string1, string2){
	result  = "";

	for(i = 0; i<string1.length; i++){
		if ( string2.indexOf(string1[i]) > -1 ) {
			result  += string1[i];
		}
	}
	return result;
}


//ROTATING AN ARRAY

//Write a function that takes an array of integers and returns that array rotated by N positions. 
//For example, if N=2, given the input array [1, 2, 3, 4, 5, 6] the function should return [5, 6, 1, 2, 3, 4]

function reverseArray(array, left, right) {
	while (left < right) {
		var temp = array[left];
		array[left] = array[right];
		array[right] = temp;
		left++;
		right--;
	}
	return array;
}

function rotate(array, k) {
	var l = array.length;
	array = reverseArray(array, 0, l - 1);
	array = reverseArray(array, 0, k - 1);
	array = reverseArray(array, k, l - 1);
	return array;
}

//ARRAY COMPACTION

//Write a function that takes as input a sorted array and modifies the array to compact it, removing duplicates. 
//Notes: The input array might be very large. 
//For example: 
//● input array = [1, 3, 7, 7, 8, 9, 9, 9, 10] 
//● transformed array = [1, 3, 7, 8, 9, 10]

function compactation(array){
	var previous = array[0];
	var i = 1;
	while(i<array.length){
		if(previous == array[i]){
			array.splice( i, 1 );
		}else{
			previous = array[i];
			i++;
		}
	}
	return array;

}


//LEAST COMMON MULTIPLE
//
//The least common multiple of a set of integers is the smallest positive integer that is a multiple of all of the 
//integers in the set. 
//Write a function that takes an array of integers and efficiently calculates and returns the LCM.

function lcm(x, y) {  
	var result = 0;
	result = Math.abs((x * y) / gcd(x, y)); 
	return  result;
}  

function gcd(x, y) {  
	x = Math.abs(x);  
	y = Math.abs(y);  
	while(y) {  
		var t = y;  
		y = x % y;  
		x = t;  
	}  
	return x;  
}  
