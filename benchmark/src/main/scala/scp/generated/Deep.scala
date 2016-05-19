package scp
package generated

object Deep {
  import scala.reflect.runtime.{universe => sru}
  import gen.TestDSLExp._
  
  import Shallow._
  
  //sru.typeOf[DSLClass1].member(sru.TermName("method1")).asMethod
  val sym1_1 = sru.typeOf[DSLClass1].member(sru.TermName("method1")).asMethod
  val sym1_2 = sru.typeOf[DSLClass1].member(sru.TermName("method2")).asMethod
  val sym1_3 = sru.typeOf[DSLClass1].member(sru.TermName("method3")).asMethod
  val sym1_4 = sru.typeOf[DSLClass1].member(sru.TermName("method4")).asMethod
  val sym1_5 = sru.typeOf[DSLClass1].member(sru.TermName("method5")).asMethod
  val sym1_6 = sru.typeOf[DSLClass1].member(sru.TermName("method6")).asMethod
  val sym1_7 = sru.typeOf[DSLClass1].member(sru.TermName("method7")).asMethod
  val sym1_8 = sru.typeOf[DSLClass1].member(sru.TermName("method8")).asMethod
  val sym1_9 = sru.typeOf[DSLClass1].member(sru.TermName("method9")).asMethod
  val sym1_10 = sru.typeOf[DSLClass1].member(sru.TermName("method10")).asMethod
  
  
  //class DSLClass1 {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = ???
  //};
  implicit class DSLClass1Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]) {
    def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = Exp[DSLClass1](methodApp(self.rep, sym1_1, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass1]));
    def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = Exp[DSLClass1](methodApp(self.rep, sym1_2, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass1]));
    def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = Exp[DSLClass1](methodApp(self.rep, sym1_3, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass1]));
    def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = Exp[DSLClass1](methodApp(self.rep, sym1_4, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass1]));
    def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = Exp[DSLClass1](methodApp(self.rep, sym1_5, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass1]));
    def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = Exp[DSLClass1](methodApp(self.rep, sym1_6, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass1]));
    def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = Exp[DSLClass1](methodApp(self.rep, sym1_7, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass1]));
    def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = Exp[DSLClass1](methodApp(self.rep, sym1_8, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass1]));
    def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = Exp[DSLClass1](methodApp(self.rep, sym1_9, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass1]));
    def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass1]): _root_.scp.gen.TestDSLExp.Exp[DSLClass1] = Exp[DSLClass1](methodApp(self.rep, sym1_10, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass1]));
  };
  implicit class DSLClass2Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]) {
    def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = Exp[DSLClass2](methodApp(self.rep, sym1_1, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass2]));
    def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = Exp[DSLClass2](methodApp(self.rep, sym1_2, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass2]));
    def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = Exp[DSLClass2](methodApp(self.rep, sym1_3, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass2]));
    def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = Exp[DSLClass2](methodApp(self.rep, sym1_4, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass2]));
    def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = Exp[DSLClass2](methodApp(self.rep, sym1_5, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass2]));
    def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = Exp[DSLClass2](methodApp(self.rep, sym1_6, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass2]));
    def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = Exp[DSLClass2](methodApp(self.rep, sym1_7, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass2]));
    def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = Exp[DSLClass2](methodApp(self.rep, sym1_8, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass2]));
    def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = Exp[DSLClass2](methodApp(self.rep, sym1_9, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass2]));
    def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = Exp[DSLClass2](methodApp(self.rep, sym1_10, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass2]));
  };
  implicit class DSLClass3Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]) {
    def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = Exp[DSLClass3](methodApp(self.rep, sym1_1, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass3]));
    def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = Exp[DSLClass3](methodApp(self.rep, sym1_2, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass3]));
    def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = Exp[DSLClass3](methodApp(self.rep, sym1_3, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass3]));
    def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = Exp[DSLClass3](methodApp(self.rep, sym1_4, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass3]));
    def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = Exp[DSLClass3](methodApp(self.rep, sym1_5, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass3]));
    def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = Exp[DSLClass3](methodApp(self.rep, sym1_6, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass3]));
    def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = Exp[DSLClass3](methodApp(self.rep, sym1_7, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass3]));
    def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = Exp[DSLClass3](methodApp(self.rep, sym1_8, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass3]));
    def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = Exp[DSLClass3](methodApp(self.rep, sym1_9, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass3]));
    def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = Exp[DSLClass3](methodApp(self.rep, sym1_10, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass3]));
  };
  implicit class DSLClass4Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]) {
    def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = Exp[DSLClass4](methodApp(self.rep, sym1_1, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass4]));
    def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = Exp[DSLClass4](methodApp(self.rep, sym1_2, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass4]));
    def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = Exp[DSLClass4](methodApp(self.rep, sym1_3, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass4]));
    def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = Exp[DSLClass4](methodApp(self.rep, sym1_4, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass4]));
    def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = Exp[DSLClass4](methodApp(self.rep, sym1_5, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass4]));
    def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = Exp[DSLClass4](methodApp(self.rep, sym1_6, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass4]));
    def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = Exp[DSLClass4](methodApp(self.rep, sym1_7, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass4]));
    def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = Exp[DSLClass4](methodApp(self.rep, sym1_8, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass4]));
    def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = Exp[DSLClass4](methodApp(self.rep, sym1_9, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass4]));
    def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = Exp[DSLClass4](methodApp(self.rep, sym1_10, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass4]));
  };
  implicit class DSLClass5Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]) {
    def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = Exp[DSLClass5](methodApp(self.rep, sym1_1, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass5]));
    def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = Exp[DSLClass5](methodApp(self.rep, sym1_2, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass5]));
    def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = Exp[DSLClass5](methodApp(self.rep, sym1_3, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass5]));
    def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = Exp[DSLClass5](methodApp(self.rep, sym1_4, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass5]));
    def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = Exp[DSLClass5](methodApp(self.rep, sym1_5, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass5]));
    def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = Exp[DSLClass5](methodApp(self.rep, sym1_6, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass5]));
    def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = Exp[DSLClass5](methodApp(self.rep, sym1_7, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass5]));
    def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = Exp[DSLClass5](methodApp(self.rep, sym1_8, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass5]));
    def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = Exp[DSLClass5](methodApp(self.rep, sym1_9, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass5]));
    def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = Exp[DSLClass5](methodApp(self.rep, sym1_10, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass5]));
  };
  implicit class DSLClass6Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]) {
    def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = Exp[DSLClass6](methodApp(self.rep, sym1_1, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass6]));
    def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = Exp[DSLClass6](methodApp(self.rep, sym1_2, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass6]));
    def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = Exp[DSLClass6](methodApp(self.rep, sym1_3, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass6]));
    def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = Exp[DSLClass6](methodApp(self.rep, sym1_4, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass6]));
    def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = Exp[DSLClass6](methodApp(self.rep, sym1_5, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass6]));
    def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = Exp[DSLClass6](methodApp(self.rep, sym1_6, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass6]));
    def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = Exp[DSLClass6](methodApp(self.rep, sym1_7, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass6]));
    def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = Exp[DSLClass6](methodApp(self.rep, sym1_8, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass6]));
    def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = Exp[DSLClass6](methodApp(self.rep, sym1_9, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass6]));
    def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = Exp[DSLClass6](methodApp(self.rep, sym1_10, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass6]));
  };
  implicit class DSLClass7Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]) {
    def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = Exp[DSLClass7](methodApp(self.rep, sym1_1, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass7]));
    def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = Exp[DSLClass7](methodApp(self.rep, sym1_2, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass7]));
    def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = Exp[DSLClass7](methodApp(self.rep, sym1_3, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass7]));
    def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = Exp[DSLClass7](methodApp(self.rep, sym1_4, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass7]));
    def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = Exp[DSLClass7](methodApp(self.rep, sym1_5, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass7]));
    def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = Exp[DSLClass7](methodApp(self.rep, sym1_6, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass7]));
    def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = Exp[DSLClass7](methodApp(self.rep, sym1_7, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass7]));
    def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = Exp[DSLClass7](methodApp(self.rep, sym1_8, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass7]));
    def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = Exp[DSLClass7](methodApp(self.rep, sym1_9, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass7]));
    def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = Exp[DSLClass7](methodApp(self.rep, sym1_10, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass7]));
  };
  implicit class DSLClass8Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]) {
    def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = Exp[DSLClass8](methodApp(self.rep, sym1_1, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass8]));
    def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = Exp[DSLClass8](methodApp(self.rep, sym1_2, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass8]));
    def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = Exp[DSLClass8](methodApp(self.rep, sym1_3, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass8]));
    def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = Exp[DSLClass8](methodApp(self.rep, sym1_4, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass8]));
    def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = Exp[DSLClass8](methodApp(self.rep, sym1_5, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass8]));
    def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = Exp[DSLClass8](methodApp(self.rep, sym1_6, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass8]));
    def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = Exp[DSLClass8](methodApp(self.rep, sym1_7, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass8]));
    def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = Exp[DSLClass8](methodApp(self.rep, sym1_8, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass8]));
    def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = Exp[DSLClass8](methodApp(self.rep, sym1_9, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass8]));
    def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = Exp[DSLClass8](methodApp(self.rep, sym1_10, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass8]));
  };
  implicit class DSLClass9Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]) {
    def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = Exp[DSLClass9](methodApp(self.rep, sym1_1, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass9]));
    def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = Exp[DSLClass9](methodApp(self.rep, sym1_2, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass9]));
    def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = Exp[DSLClass9](methodApp(self.rep, sym1_3, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass9]));
    def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = Exp[DSLClass9](methodApp(self.rep, sym1_4, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass9]));
    def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = Exp[DSLClass9](methodApp(self.rep, sym1_5, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass9]));
    def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = Exp[DSLClass9](methodApp(self.rep, sym1_6, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass9]));
    def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = Exp[DSLClass9](methodApp(self.rep, sym1_7, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass9]));
    def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = Exp[DSLClass9](methodApp(self.rep, sym1_8, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass9]));
    def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = Exp[DSLClass9](methodApp(self.rep, sym1_9, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass9]));
    def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = Exp[DSLClass9](methodApp(self.rep, sym1_10, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass9]));
  };
  implicit class DSLClass10Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]) {
    def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = Exp[DSLClass10](methodApp(self.rep, sym1_1, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass10]));
    def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = Exp[DSLClass10](methodApp(self.rep, sym1_2, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass10]));
    def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = Exp[DSLClass10](methodApp(self.rep, sym1_3, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass10]));
    def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = Exp[DSLClass10](methodApp(self.rep, sym1_4, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass10]));
    def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = Exp[DSLClass10](methodApp(self.rep, sym1_5, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass10]));
    def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = Exp[DSLClass10](methodApp(self.rep, sym1_6, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass10]));
    def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = Exp[DSLClass10](methodApp(self.rep, sym1_7, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass10]));
    def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = Exp[DSLClass10](methodApp(self.rep, sym1_8, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass10]));
    def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = Exp[DSLClass10](methodApp(self.rep, sym1_9, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass10]));
    def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = Exp[DSLClass10](methodApp(self.rep, sym1_10, Nil, Args(x.rep)::Nil, typeRepOf[DSLClass10]));
  };
  
  
  
  //class DSLClass2 {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???
  //};
  //implicit class DSLClass2Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]) {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass2]): _root_.scp.gen.TestDSLExp.Exp[DSLClass2] = ???
  //};
  //class DSLClass3 {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???
  //};
  //implicit class DSLClass3Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]) {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass3]): _root_.scp.gen.TestDSLExp.Exp[DSLClass3] = ???
  //};
  //class DSLClass4 {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???
  //};
  //implicit class DSLClass4Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]) {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass4]): _root_.scp.gen.TestDSLExp.Exp[DSLClass4] = ???
  //};
  //class DSLClass5 {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???
  //};
  //implicit class DSLClass5Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]) {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass5]): _root_.scp.gen.TestDSLExp.Exp[DSLClass5] = ???
  //};
  //class DSLClass6 {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???
  //};
  //implicit class DSLClass6Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]) {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass6]): _root_.scp.gen.TestDSLExp.Exp[DSLClass6] = ???
  //};
  //class DSLClass7 {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???
  //};
  //implicit class DSLClass7Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]) {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass7]): _root_.scp.gen.TestDSLExp.Exp[DSLClass7] = ???
  //};
  //class DSLClass8 {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???
  //};
  //implicit class DSLClass8Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]) {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass8]): _root_.scp.gen.TestDSLExp.Exp[DSLClass8] = ???
  //};
  //class DSLClass9 {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???
  //};
  //implicit class DSLClass9Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]) {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass9]): _root_.scp.gen.TestDSLExp.Exp[DSLClass9] = ???
  //};
  //class DSLClass10 {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???
  //};
  //implicit class DSLClass10Ops(self: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]) {
  //  def method1(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method2(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method3(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method4(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method5(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method6(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method7(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method8(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method9(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???;
  //  def method10(x: _root_.scp.gen.TestDSLExp.Exp[DSLClass10]): _root_.scp.gen.TestDSLExp.Exp[DSLClass10] = ???
  //};
  
}

