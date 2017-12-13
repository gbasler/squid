// Copyright 2017 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid
package classembed.geom

import utils._
import quasi.{embed, phase}
import squid.utils.CollectionUtils.TraversableOnceHelper

/**
  * Q: put all in a Geom class parametrized with the arity?
  *     I guess not, as it would restrict usage significantly (only square matrices) 
  * 
  */
@embed
@phase('Arithmetics)
//case class Vector(coords: Array[Num]) {
class Vector(val coords: List[Num]) {
  
  @phase('Sugar)
  def arity = coords.length
  
  @phase('Arithmetics)
  def * (that: Vector) = {
    assert(arity == that.arity)
    (coords zipAnd that.coords)(_ * _) reduce (_ + _)
  }
  
  override def toString: String = s"[${coords mkString " "}]"
}
object Vector {
  
  def origin(arity: Int) = new Vector(List.fill(arity)(0))
  
  // TODO varargs
  @phase('Sugar)
  def apply(values: Num*) = new Vector(List(values: _*))
  
}

@embed
//case class Matrix(lines: Array[Vector]) {
class Matrix(val lines: List[Vector]) {
  @phase('Sugar)
  def lineCount = lines.length
  
  override def toString: String = s"${lines mkString "\n"}"
}
object Matrix {
  
  def empty(lineCount: Int, colCount: Int) = new Matrix(List.fill(lineCount) { new Vector(List.fill(colCount)(0)) })
  
  // TODO varargs
  @phase('Sugar)
  def apply(lines: Vector*) = new Matrix(List(lines: _*))
  
  def identity(arity: Int) = new Matrix({for (i <- 0 until arity) yield new Vector({for (j <- 0 until arity) yield if (i == j) (1:Num) else (0:Num)} toList)} toList)
  //def identity(size: Int) = { var i = 0; Matrix(Array.fill(size) {
  //    var j = 0; Vector(Array.fill(size){(if (i == j) 1 else 0) oh_and (j += 1)}) oh_and (i += 1)
  //  })}
  
}




