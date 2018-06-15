package nl.uu.fi.dwo.rest.dom.entities;

@SuppressWarnings("rawtypes")
public class DomResultStudentScoPage extends DomResultScore {

  public DomResultStudentScoPage(String label) {
      setLabel(label);
  }

  @Override
  public String getId() {
    return getLabel();
  }

}
