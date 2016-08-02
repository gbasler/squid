package scp
package lang2

trait InspectableBase extends IntermediateBase {
  
  def hole(name: String, typ: TypeRep): Rep
  def splicedHole(name: String, typ: TypeRep): Rep
  
  
  
}
