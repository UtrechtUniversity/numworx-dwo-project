package fi.wiskopdr.expressies.repr;

import java.util.ArrayList;
import java.util.Objects;

import fi.wiskopdr.expressies.Expressie;

public class CasCheck extends AbstractConverter {

  private CasCheck() {
  }
  
  static public CasCheck getInstance() {
    return new CasCheck();
  }

  @Override
  public Object abs(Object visit) {
    return visit;
  }

  @Override
  public Object expressie(Expressie expressie) {
    return "IAD";
  }

  @Override
  public Object arccos(Object kind1) {
    return kind1;
  }

  @Override
  public Object arcsin(Object kind1) {
    return kind1;
  }

  @Override
  public Object arctan(Object kind1) {
    return kind1;
  }

  @Override
  public Object bin(Object kind1, Object kind2) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "");
  }

  @Override
  public Object binomcdf(Object kind1, Object kind2, Object kind3) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "") + Objects.toString(kind3, "");
  }

  @Override
  public Object binompdf(Object kind1, Object kind2, Object kind3) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "") + Objects.toString(kind3, "");
  }

  @Override
  public Object conjug(Object kind1) {
    return kind1;
  }

  @Override
  public Object cosecans(Object kind1) {
    return kind1;
  }

  @Override
  public Object cosinus(Object kind1) {
    return kind1;
  }

  @Override
  public Object cotangens(Object kind1) {
    return kind1;
  }

  @Override
  public Object decround(Object kind1, Object kind2) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "");
  }

  @Override
  public Object diff(Object kind1, Object kind2) {
    return "$d" + Objects.toString(kind1, "") + Objects.toString(kind2, "") +"@";
  }

  @Override
  public Object differentiaal(Object kind1) {
    return "$g" + Objects.toString(kind1, "") +"@";
  }

  @Override
  public Object fac(Object kind1) {
    return kind1;
  }

  @Override
  public Object gcd(Object kind1, Object kind2) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "");
  }

  @Override
  public Object integrate(Object kind1, Object kind2, Object kind3, Object kind4, String string) {
    return "$i@";
  }

  @Override
  public Object invNorm(Object kind1, Object kind2, Object kind3) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "") + Objects.toString(kind3, "");
  }

  @Override
  public Object limit(Object kind1, Object kind2, Object kind3, Object kind4) {
    return "$T@";
  }

  @Override
  public Object ln(Object kind1) {
    return kind1;
  }

  @Override
  public Object log(Object kind1) {
    return kind1;
  }

  @Override
  public Object max(Object kind1, Object kind2) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "");
  }

  @Override
  public Object min(Object kind1, Object kind2) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "");
  }

  @Override
  public Object ndelog(Object kind1, Object kind2) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "");
  }

  @Override
  public Object root(Object kind1, Object kind2) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "");
  }

  @Override
  public Object normalcdf(Object kind1, Object kind2, Object kind3, Object kind4) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "") + Objects.toString(kind3, "") + Objects.toString(kind4, "");
  }

  @Override
  public Object poissoncdf(Object kind1, Object kind2) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "");
  }

  @Override
  public Object poissonpdf(Object kind1, Object kind2) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "");
  }

  @Override
  public Object primitieve(Object kind1, Object kind2, String string) {
    return "$P" + Objects.toString(kind1,"") + Objects.toString(kind2,"")  + "@";
  }

  @Override
  public Object prv(Object kind1, Object kind2, Object kind3, Object kind4) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "") + Objects.toString(kind3, "") + Objects.toString(kind4, "");
  }

  @Override
  public Object secans(Object kind1) {
    return kind1;
  }

  @Override
  public Object sigma(Object kind1, Object kind2, Object kind3, Object kind4) {
    return "$S@";
  }

  @Override
  public Object siground(Object kind1, Object kind2, Object kind3) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "") + Objects.toString(kind3, "");
  }

  @Override
  public Object sigroundstandard(Object kind1, Object kind2) {
    return Objects.toString(kind1, "") + Objects.toString(kind2, "");
  }

  @Override
  public Object sinus(Object kind1) {
    return kind1;
  }

  @Override
  public Object tangens(Object kind1) {
    return kind1;
  }

  @Override
  public Object wortel(Object kind1) {
    return kind1;
  }

  @Override
  public Object aantalsign(Object visit) {
    return visit;
  }

  @Override
  public Object vectorExpr(ArrayList<Object> kinderen) {
    StringBuilder sb = new StringBuilder();
    for(Object item: kinderen) if (item != null) sb.append(item);
    return sb;
  }

  @Override
  public Object matrix(ArrayList<ArrayList<Object>> kinderen) {
    StringBuilder sb = new StringBuilder();
    for(ArrayList<Object> item: kinderen) if (item != null) sb.append(vectorExpr(item));
    return sb;
  }

}
