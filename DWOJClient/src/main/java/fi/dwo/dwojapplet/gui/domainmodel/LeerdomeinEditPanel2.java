package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicMenuBarUI;
import javax.swing.plaf.basic.BasicMenuUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import fi.beans.dwomaccess.JSONEncoder;
import fi.beans.mainframe.AppletStub;
import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JCheckBox;
import fi.beans.numworxlf.JComboBox;
import fi.beans.numworxlf.JFormattedTextField;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTextField;
import fi.beans.numworxlf.JTree;
import fi.beans.private_base64code.StringCodeObject;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.ConfirmDialog;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.TeacherStudentModelPanelProperties;
import fi.dwo.dwojapplet.gui.action.GuiAction;
import fi.dwo.dwojapplet.gui.domainmodel.ExportAction.ExportPanel;
import fi.dwo.dwojapplet.gui.domainmodel.LeerdomeinEditPanel2.VariantListener;
import fi.dwo.dwojapplet.gui.domainmodel.graph.EditableGraph;
import fi.dwo.dwojapplet.gui.domainmodel.graph.Graph;
import fi.dwo.dwojapplet.gui.domainmodel.graph.TreeTransferHandler;
import fi.dwo.dwojapplet.gui.domainmodel.methods.KoppelPanel;
import fi.dwo.dwojapplet.gui.domainmodel.methods.MethodsProperties;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdr;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrCache;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrEditPanel;
import fi.dwo.dwojapplet.gui.wiskopdr.WiskOpdrPanel;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherMethodManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelMethodInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelVariant;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.StudentModelUtil;

public class LeerdomeinEditPanel2 extends JPanel
		implements TreeSelectionListener, ExportPanel, WindowListener, ItemListener {
	
	public class VariantListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				DomStudentModelVariant v = (DomStudentModelVariant) e.getItem();
				NodeLeaf leaf = (NodeLeaf) ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject();
				leaf.setVariant(v);
				if (!editable) {
					setDescription(leaf);
					validate();
					repaint();
				} else {
					Map<String, Boolean> layersinfo = v.getLayers();
					wiskOpdrEditPanel.setLayersVisible(layersinfo);
					Action a = v.getName() == null ? voorkennisActionRW : deselectionsActionRW;
					voorkennis.setAction(a);
				}
			}

		}

	}

	class RenameVariantAction extends AbstractAction {
		RenameVariantAction() {
			super("Hernoem variant...");
			//setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			NodeLeaf leaf = (NodeLeaf) ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject();
			DomStudentModelVariant selected = (DomStudentModelVariant) variantBox.getSelectedItem();
			if (selected != null && selected.getName()!= null) {
				String ok = JOptionPane.showInputDialog(LeerdomeinEditPanel2.this, "Hernoemen", selected.getName());
				if (ok != null) {
					List<DomStudentModelMethodInfo> methods = leaf.getMethodeInfos();
					WrappedSet variants = leaf.getVariants();
					for(DomStudentModelVariant m : variants) {
						if (ok.equals(m.getName())) return; // backout 
					}				
					LOG.info("rename " + selected.getName() + " to " + ok);
					for(DomStudentModelMethodInfo m : methods) {
						if (selected.getName().equals(m.getVariant())) m.setVariant(ok);
					}
					selected.setName(ok);
					variantBox.repaint();
				}
			}
		}
	}
	
	
	class RemoveVariantAction extends AbstractAction {
		RemoveVariantAction() {
			super("Verwijder variant...");
			setEnabled(false);
		}
		public void actionPerformed(ActionEvent e) {
			NodeLeaf leaf = (NodeLeaf) ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject();
			DomStudentModelVariant selected = (DomStudentModelVariant) variantBox.getSelectedItem();
			if (selected != null && selected.getName()!= null) {
				int ok = JOptionPane.showConfirmDialog(LeerdomeinEditPanel2.this, selected.getName() + " verwijderen?", "Verwijderen variant", JOptionPane.OK_CANCEL_OPTION);
				if (ok == JOptionPane.OK_OPTION) {
					leaf.getVariants().remove(selected);
					leaf.setDefaultVariant();
					variantBox.removeItem(selected);
					variantBox.setSelectedIndex(0);
					if (leaf.getVariants().size() == 1) {
						variantBox.setVisible(false);
						setEnabled(false);
					}
					List<DomStudentModelMethodInfo> methods = leaf.getMethodeInfos();
					for(DomStudentModelMethodInfo m : methods) {
						if (selected.getName().equals(m.getVariant())) m.setVariant(null);
					}
			}}
		}
 	}
	
	
	
	
	@SuppressWarnings("serial")
	class CreateVariantAction extends AbstractAction {
		CreateVariantAction() {
			super("Nieuwe variant...");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JPanel panel = new JPanel();
			// vullen
			panel.add(new JLabel("Variant"));
			JTextField name = new JTextField(); name.setColumns(10);
			panel.add(name);
			int r = JOptionPane.showConfirmDialog(LeerdomeinEditPanel2.this, panel, toString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (r == JOptionPane.OK_OPTION && !name.getText().trim().isEmpty()) {
				LOG.info("selected " + name.getText());
				DomStudentModelVariant n = new DomStudentModelVariant(name.getText().trim());
				MutableComboBoxModel<DomStudentModelVariant> model = (MutableComboBoxModel<DomStudentModelVariant>) variantBox.getModel();
				NodeLeaf leaf = (NodeLeaf) ((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject();
				if (leaf.getVariants().add(n)) {
					if (model.getSize() == 0) model.addElement(new DomStudentModelVariant());
					model.addElement(n);
					leaf.setVariant(model.getElementAt(0));
					variantBox.setVisible(true);
				};
			}

		}
		
		public String toString() {
			return getValue(NAME).toString();
		}
	}

	public static final Integer DEFAULT_NODE_SIZE = 24;
    static final String WISKOPDR_SIG = "H4sIAAAAAA";
	static final Logger LOG = Logger.getLogger(LeerdomeinEditPanel2.class.getName());
	
	static class DeselectionAction extends AbstractAction {

		private DeselectionAction() {
			super(TextMapper.getText("Voorkennis Variant"));
		}

		DeselectionAction(boolean b, Component parent, JTree tree, EditableGraph graph) {
			this();
			readonly = b;
			this.parent = parent;
			this.tree = tree;
			this.model = tree.getModel();
			this.graph = graph;
			
		}
		boolean readonly;
		JTree tree;
		Component parent;
        private EditableGraph graph;
        private TreeModel model;

		@Override
		public void actionPerformed(ActionEvent e) {
			DomStudentModelVariant variant;
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return;
			if(!readonly) graph.updateModel(model);
			
			InvisibleNode root;
			root = (InvisibleNode) model.getRoot();
			Map<String, NodeLeaf> leafs = getLeafs((InvisibleTreeModel) model, root);
			Object node = path.getLastPathComponent();
			if (node instanceof MutableTreeNode) {
				InvisibleNode mutable = (InvisibleNode) node;
				Object o = mutable.getUserObject();
				if (o instanceof NodeLeaf) {
					NodeLeaf leaf = (NodeLeaf) o;
					variant = leaf.getVariant();
					Collection<String> ids = leaf.getVoorkennis();
					ids = closure(ids, leafs);
					ids = Graph.strip(ids);
					List<String> copy = new ArrayList<>(ids);
					copy.removeAll(StudentModelUtil.strip(variant.getDeselections()));
					Set<String> org = new TreeSet<>(ids);
					copy.add(leaf.getId());
					NodeVector v = (NodeVector) root.getUserObject();
					StudentModelChoicePanel panel = new StudentModelChoicePanel(v, readonly, org);
					panel.setObjectives(copy);
					if (readonly) {
						// JOptionPane.showMessageDialog(parent, panel, e.getActionCommand(),
						// JOptionPane.PLAIN_MESSAGE);
						ConfirmDialog d = new ConfirmDialog(parent, e.getActionCommand());
						d.setContentPane(panel);
						d.pack();
						d.center();
						d.show();
					} else {
						// int r = JOptionPane.showConfirmDialog(parent, panel, e.getActionCommand(),
						// JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

						ConfirmDialog confirm = new ConfirmDialog(parent, e.getActionCommand());
						confirm.getContentPane().setLayout(new BorderLayout());
						confirm.getContentPane().add(panel);
						JButton okb = new JButton(TextMapper.getText(TextMapper.BTN_OK));
						okb.addActionListener(confirm::ok);
						okb.setBackground(GuiConstants.HEADER_COLOR);
						JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
						south.setBackground(Constants.COLOR21);
						south.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
						south.add(okb);
						JButton cancel = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
						cancel.addActionListener(confirm::cancel);
						cancel.setBackground(GuiConstants.HEADER_COLOR);
						okb.setPreferredSize(cancel.getPreferredSize());
						south.add(cancel);
						confirm.getContentPane().add(south, BorderLayout.SOUTH);
						confirm.pack();
						confirm.center();
						confirm.show();
						int r = confirm.getOption();

						if (r == JOptionPane.OK_OPTION) {
							panel.makeChoices();
							Collection<String> list = /*Graph.strip*/(panel.getObjectives());
							org.removeAll(list);
							variant.setDeselections(org);
							leaf.setVariant(variant);
							graph.setModel(tree.getModel(),null, null);
						}
					}
				}
			}
		}

		protected List<String> closure(Collection<String> ids, Map<String, NodeLeaf> leafs) {
			if (ids == null)
				return Collections.emptyList();
			Function<String, Stream<String>> f = id -> {
				NodeLeaf leaf = leafs.get(id); // assume strip
				if (leaf == null)
					return Stream.empty();
				return closure(StudentModelUtil.strip(leaf.getVoorkennis()), leafs).stream(); // voorkennis is niet gestript
				};
			List<String> extra = ids.stream().flatMap(f ).collect(Collectors.toList());
			extra.addAll(ids);
			return extra;
		}

		
		// FIXME DEZE IS NIET GOED, gebruikt "invisible nodes" niet en we willen alles
		private Map<String, NodeLeaf> getLeafs(InvisibleTreeModel model, Object node) {
			boolean old = model.isActivatedFilter();
			try {
				model.activateFilter(false); // heeft effect op getchildcount, etc.
				Map<String, NodeLeaf> result = new HashMap<>();
				int cnt = model.getChildCount(node);
				for(int i = 0; i < cnt; i++) {
					Object child = model.getChild(node, i);
					result.putAll(getLeafs(model, child));
					Object object = ((DefaultMutableTreeNode) child).getUserObject();
					if (object instanceof NodeLeaf) {
						NodeLeaf l = (NodeLeaf) object;
						result.put(l.getId(), l);
					}
				}
				return result;
			} finally {
				model.activateFilter(old);
			}
		}
		
	}
	
	
	
	

	static class VoorkennisAction extends AbstractAction {

		boolean readonly;
		JTree tree;
		Component parent;
        private EditableGraph graph;
        private TreeModel model;

		private VoorkennisAction() {
			super(TextMapper.getText("Voorkennis"));
		}

		public VoorkennisAction(boolean b, Component parent, JTree tree, EditableGraph graph) {
			this();
			readonly = b;
			this.parent = parent;
			this.tree = tree;
			this.model = tree.getModel();
			this.graph = graph;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return;
			if(!readonly) graph.updateModel(model);
			
			InvisibleNode root;
			root = (InvisibleNode) model.getRoot();

			Object node = path.getLastPathComponent();
			if (node instanceof MutableTreeNode) {
				InvisibleNode mutable = (InvisibleNode) node;
				Object o = mutable.getUserObject();
				if (o instanceof NodeLeaf) {
					NodeLeaf leaf = (NodeLeaf) o;
					List<String> ids = leaf.getVoorkennis();
					if (ids == null)
						ids = Collections.emptyList();
					NodeVector v = (NodeVector) root.getUserObject();
					StudentModelChoicePanel panel = new StudentModelChoicePanel(v, readonly);
					panel.setObjectives(ids);
					if (readonly) {
						// JOptionPane.showMessageDialog(parent, panel, e.getActionCommand(),
						// JOptionPane.PLAIN_MESSAGE);
						ConfirmDialog d = new ConfirmDialog(parent, e.getActionCommand());
						d.setContentPane(panel);
						d.pack();
						d.center();
						d.show();
					} else {
						// int r = JOptionPane.showConfirmDialog(parent, panel, e.getActionCommand(),
						// JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

						ConfirmDialog confirm = new ConfirmDialog(parent, e.getActionCommand());
						confirm.getContentPane().setLayout(new BorderLayout());
						confirm.getContentPane().add(panel);
						JButton okb = new JButton(TextMapper.getText(TextMapper.BTN_OK));
						okb.addActionListener(confirm::ok);
						okb.setBackground(GuiConstants.HEADER_COLOR);
						JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
						south.setBackground(Constants.COLOR21);
						south.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
						south.add(okb);
						JButton cancel = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
						cancel.addActionListener(confirm::cancel);
						cancel.setBackground(GuiConstants.HEADER_COLOR);
						okb.setPreferredSize(cancel.getPreferredSize());
						south.add(cancel);
						confirm.getContentPane().add(south, BorderLayout.SOUTH);
						confirm.pack();
						confirm.center();
						confirm.show();
						int r = confirm.getOption();

						if (r == JOptionPane.OK_OPTION) {
							panel.makeChoices();
							List<String> list = panel.getObjectives();
							leaf.setVoorkennis(list);
							graph.setModel(tree.getModel(),null, null);
						}
					}
				}
			}
		}

	}

	class LeerdoelAction extends AbstractAction {

		public LeerdoelAction() {
			this("nieuw leerdoel");
		}

		public LeerdoelAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return;

			Object node = path.getLastPathComponent();
			if (root == node)
				return;

			if (node instanceof MutableTreeNode) {
				MutableTreeNode mutable = (MutableTreeNode) node;
				if (!mutable.getAllowsChildren())
					return;
				boolean old = model.isActivatedFilter();
				model.activateFilter(false);
				int index = model.getChildCount(mutable);
				model.activateFilter(old);
				Node leaf;
				String title = "Leerdoel-" + (index + 1);
				if (untitledObjective != null) {
					leaf = new NodeLeaf(title, untitledObjective.getInfo(), getLocale().getLanguage());
				} else {
					leaf = new NodeLeaf(getLocale().getLanguage());
					leaf.setTitle(title);
				}
				InvisibleNode child = new InvisibleNode(leaf, false, true);
				mutable.insert(child, index);
				index = model.getChildCount(mutable)-1;
				model.nodesWereInserted(mutable, new int[] { index });
				tree.setSelectionPath(new TreePath(child.getPath()));
				subtitle.requestFocusInWindow();
				subtitle.selectAll();
			}
		}

	}

	public class SubdomeinAction extends AbstractAction {

		public SubdomeinAction() {
			this("nieuw subdomein");
		}

		public SubdomeinAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return;

			Object node = path.getLastPathComponent();
			// if ( node != root) return; // Only root can add subdomains

			if (node instanceof MutableTreeNode) {
				MutableTreeNode mutable = (MutableTreeNode) node;
				if (!mutable.getAllowsChildren())
					return;
				boolean old = model.isActivatedFilter();
				model.activateFilter(false);
				int index = model.getChildCount(node);
				model.activateFilter(old);
				
				Node vector;
				if (untitledCategory != null) {
					vector = new NodeVector(untitledCategory.getInfo(), getLocale().getLanguage());
				} else {
					vector = new NodeVector(getLocale().getLanguage());
				}
				vector.setTitle("Untitled-" + (index + 1));
				InvisibleNode child = new InvisibleNode(vector, true, true);
				InvisibleNode.createEmptyChildren(child);
				mutable.insert(child, index);
				index = model.getChildCount(mutable)-1;
				model.nodesWereInserted(mutable, new int[] { index });
				tree.setSelectionPath(new TreePath(child.getPath()));
				subtitle.requestFocusInWindow();
				subtitle.selectAll();
			}
		}

	}

	class Verwijderen extends AbstractAction {

		Verwijderen() {
			this(TextMapper.getText("delete"));
		}

		Verwijderen(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return;

			Object node = path.getLastPathComponent();
			if (node == root)
				return;
			if (node instanceof MutableTreeNode) {
				InvisibleNode mutable = (InvisibleNode) node;
				TreeNode parent = mutable.getParent();
				mutable.removeFromParent();
				model.nodeStructureChanged(parent);
				fillSelection();
			}
		}

	}

	class Omhoog extends AbstractAction {
		Omhoog() {
			super("Omhoog");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return;
			InvisibleNode node = (InvisibleNode) path.getLastPathComponent();
			InvisibleNode parent = (InvisibleNode) node.getParent();
			int i = parent.getIndex(node);
			if (i > 0) {
				safeSelection(path);
				parent.remove(i);
				parent.insert(node, i - 1);
				model.nodeStructureChanged(parent);
				tree.setSelectionPath(new TreePath(node.getPath()));
				tree.repaint();
			}

		}

	}

	class Omlaag extends AbstractAction {
		Omlaag() {
			super("Omlaag");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return;
			InvisibleNode node = (InvisibleNode) path.getLastPathComponent();
			InvisibleNode parent = (InvisibleNode) node.getParent();
			int i = parent.getIndex(node);
			if (i < parent.getChildCount() - 1) {
				safeSelection(path);
				parent.remove(i);
				parent.insert(node, i + 1);
				model.nodeStructureChanged(parent);
				tree.setSelectionPath(new TreePath(node.getPath()));
				tree.repaint();
			}
		}
	}

	class MethodeAction extends AbstractAction {

    MethodeAction() {
	    super("Koppel Lesmethoden");
	  }

      @Override
      public void actionPerformed(ActionEvent e) {
        LeerdomeinEditPanel2 parent = LeerdomeinEditPanel2.this;
        KoppelPanel panel = new KoppelPanel();
        List<String> k = koppeling;
        List<String> NULL = cacheMethods(structure);
        if (k == null) k = NULL;
        panel.setMethods(k);
        int ok = panel.showDialog(parent);
        if (ok == JOptionPane.OK_OPTION) {
          koppeling = panel.getMethods();
          initMethodSelect(koppeling);
          if (NULL.equals(koppeling)) koppeling = null;
       }
      }
	}
	
	class StandardAction extends GuiAction {
	  StandardAction() {
	    super("Maak standaard leerdomein");
	    setEnabled(hasAdminRight());
	  }
	  
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    int ok = JOptionPane.showConfirmDialog(LeerdomeinEditPanel2.this, "Weet je het echt heel zeker?", (String) getValue(NAME), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	    if (ok == JOptionPane.OK_OPTION) {
          try {
            opslaanAction(e);
            setEditable(false);
            DomMethod dom = MethodsProperties.instance().getMethod(activeMethod);
            if (dom != null && !dom.standard) {
              dom.standard = true;
              SecureTeacherMethodManager.updateModel(dom, DWO.getDwoProfile());
            }
            setModel(structure,PublishState.overt);
            prop.getCurrent().setPublishState(PublishState.overt);
            prop.updateModel(prop.getCurrent().getModelStructure());
          } catch (Dwo2Exception e1) {
            LOG.log(Level.SEVERE, "update to overt mode", e1);
          }
	    }
	  }
	}
	
	
	
	
	InvisibleNode clipboard;

	class Knippen extends AbstractAction {
		Knippen() {
			super(TextMapper.getText("cut"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return;
			Object node = path.getLastPathComponent();
			if (node == root)
				return;
			if (node instanceof MutableTreeNode) {
				safeSelection(path);
				InvisibleNode mutable = (InvisibleNode) node;
				TreeNode parent = mutable.getParent();
				mutable.removeFromParent();
				clipboard = mutable;
				model.nodeStructureChanged(parent);
				fillSelection();

			}
		}

	}

	class Kopieren extends AbstractAction {
		Kopieren() {
			super(TextMapper.getText("copy"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return;

			Object node = path.getLastPathComponent();
			if (node == root)
				return;
			safeSelection(path);
			clipboard = copy(node);
		}

	}

	public InvisibleNode copy(Object node) {
		if (node instanceof InvisibleNode) {
			InvisibleNode mutable = (InvisibleNode) node;
			if (mutable.isLeaf() && !mutable.getAllowsChildren()) {
				return new InvisibleNode(new NodeLeaf((NodeLeaf) mutable.getUserObject()), false, true);
			} else {
				NodeVector v = new NodeVector((NodeVector) mutable.getUserObject());
				InvisibleNode copy = new InvisibleNode(v, true, true);
				InvisibleNode.createEmptyChildren(copy);
				for (int i = 0; i < mutable.getChildCount(); i++) {
					copy.add(copy(mutable.getChildAt(i)));
				}
				return copy;
			}
		}
		return null;
	}

	public void filter(Map<String, Map<String, Set<Integer>>> filter) {
		if (graph.isShowing())
			graph.updateModel(model);
		if (filter.isEmpty()) {
			model.activateFilter(false);
			if (model.getRoot() != root)
				model.setRoot(root);
		} else {
			model.activateFilter(true);
			model.setRoot(filter(root, filter, activeMethod));
		}
        model.nodeStructureChanged((TreeNode) model.getRoot());
		graph.setModel(model,filter, activeMethod);
		methodListener.filterMethod(filter);
	}

	
	static InvisibleNode filter(InvisibleNode parent, Map<String, Map<String, Set<Integer>>> filter, PersistenceId activeMethod) {
		InvisibleNode node;
		node = parent;
          @SuppressWarnings("unchecked")
          Enumeration<InvisibleNode> children = (Enumeration) node.children();
          while (children.hasMoreElements()) {
              InvisibleNode object = children.nextElement();
              filter(object, filter, activeMethod);
          }
		if (node.isLeaf() && !node.getAllowsChildren()) {
			NodeLeaf leaf = (NodeLeaf) node.getUserObject();
			Map<String, Map<String, Set<Integer>>> methodes = leaf.getMethode();
			node.setVisible(contains(filter, methodes, activeMethod));
		} else {
			int cnt = node.getChildCount(true);
			node.setVisible(cnt != 0);
		}

		return node;
	}

	static boolean contains(Map<String, Map<String, Set<Integer>>> filter,
			Map<String, Map<String, Set<Integer>>> methodes, PersistenceId activeMethod) {
	    String currentKey = DomMethod.key(activeMethod);
		for (Map.Entry<String, Map<String, Set<Integer>>> entry : filter.entrySet()) {
		    if (entry.getKey() == null) {
		      //if (methodes.values().stream().allMatch(Map::isEmpty)) return true;
              if ( methodes.entrySet().stream().allMatch(e -> e.getValue().isEmpty()||!e.getKey().equals(currentKey))) return true;
		      continue;
		    }		  
			Map<String, Set<Integer>> map = methodes.getOrDefault(entry.getKey(), Collections.emptyMap());
			if (map.isEmpty())
			{ 
			  continue;
			}
			for (Map.Entry<String, Set<Integer>> m : entry.getValue().entrySet()) {
				Set<Integer> chapters = new TreeSet<>(map.getOrDefault(m.getKey(), Collections.emptySet()));
				chapters.retainAll(m.getValue());
				if (!chapters.isEmpty())
					return true;
			}
		}
		return false;
	}

	class Plakken extends AbstractAction {
		Plakken() {
			super(TextMapper.getText("paste"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (clipboard == null)
				return;
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return;

			Object node = path.getLastPathComponent();
			if (node == root) {
				if (!clipboard.getAllowsChildren())
					return;
				root.add(clipboard);
				model.nodeStructureChanged(root);
				tree.setSelectionPath(new TreePath(clipboard.getPath()));
				clipboard = copy(clipboard);
				tree.repaint();
				return;
			}
			if (node instanceof MutableTreeNode) {
				MutableTreeNode mutable = (MutableTreeNode) node;
				if (!mutable.getAllowsChildren()) {
					mutable = (MutableTreeNode) mutable.getParent();
				}
				((InvisibleNode) mutable).add(clipboard);
				model.nodeStructureChanged(mutable);
				tree.setSelectionPath(new TreePath(clipboard.getPath()));
				tree.repaint();
				clipboard = copy(clipboard);
				return;
			}
		}

	}

	private JButton okButton;
	private DomStudentModelStructure structure;
	private JMenuBar bar = new JMenuBar();
	private JLabel title;
	private JTextField subtitle;
	private JButton bewerken;
	private JButton graphButton;
	private Box south;
	final JTree tree;
	final InvisibleTreeModel model;
	PersistenceId activeMethod;
	InvisibleNode root;
	private JComponent settings;
	JFormattedTextField slip, init, learn;
	private JComboBox<Integer> nodeSizeChoice;
	final private EditableGraph graph;

	private Box settingsRO;
	private JPanel settingsRW;
	private final TeacherStudentModelPanelProperties prop;
    private JComboBox<DomMethod> methodSelect;
    private List<String> koppeling = Collections.emptyList();
	private DeselectionAction deselectionsActionRW;

	private static final Font font = new Font("SansSerif", Font.PLAIN, 12);

	public LeerdomeinEditPanel2(TeacherStudentModelPanelProperties prop) {
		super(new BorderLayout());
		this.prop = prop;

		south = Box.createHorizontalBox();
		south.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		south.setOpaque(true);
		south.setBackground(Constants.COLOR21);
		okButton = new JButton(TextMapper.getText(TextMapper.GUIP_BTN_SAVE));
		okButton.setPreferredSize(new Dimension(100, 24));
		okButton.setBackground(Constants.COLOR15);
		okButton.setForeground(Constants.COLOR20);
		okButton.addActionListener(this::opslaanAction);
		south.add(Box.createHorizontalGlue());
		south.add(okButton);
		south.add(Box.createHorizontalGlue());

		add(south, BorderLayout.SOUTH);

		Box north = Box.createHorizontalBox();
		north.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
		north.setOpaque(true);
		north.setBackground(Constants.COLOR15);
		bewerken = new JButton(TextMapper.getText(TextMapper.GUIH_EDIT));
		graphButton = new JButton("Graph");
		title = new JLabel(getTitle());
		title.setForeground(Constants.COLOR20);
		title.setFont(font.deriveFont(24f));
		north.add(bewerken);
		north.add(Box.createHorizontalGlue());
		north.add(title);
		north.add(Box.createHorizontalGlue());
		north.add(graphButton);
		bewerken.addActionListener(e -> {
			if (TextMapper.getText(TextMapper.GUIH_STOP_EDIT) != bewerken.getText()) {
				if (aquireLock())
					setEditable(true);
			} else {
				switch (confirm()) {
				case JOptionPane.YES_OPTION:
					opslaanAction(null);
				case JOptionPane.NO_OPTION:
					setEditable(false);
					setModel0(structure);
				case JOptionPane.CANCEL_OPTION:
				}
			}
		});

		add(north, BorderLayout.NORTH);

		JSplitPane split = new JSplitPane();
		BasicSplitPaneUI sui = (BasicSplitPaneUI) BasicSplitPaneUI.createUI(split);
		split.setUI(sui);
		BasicSplitPaneDivider divider = sui.getDivider();
		divider.setBorder(BorderFactory.createEmptyBorder());
		divider.setBackground(Constants.COLOR20);
		split.setDividerSize(20);
		split.setResizeWeight(0.8);
		split.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		split.setBackground(Constants.COLOR20);
		setBackground(Constants.COLOR20);
		JMenu Bestand = new JMenu(TextMapper.getText("file"));
		JMenu Bewerken = new JMenu(TextMapper.getText("edit"));
		JMenu Instellingen = new JMenu(TextMapper.getText("Instellingen"));
		bar.setBackground(Constants.COLOR21);
		bar.setBorder(BorderFactory.createEmptyBorder());
		Dimension pref = bar.getPreferredSize();
		pref.height = 26; // same as textfield subtitle.
		bar.setPreferredSize(pref);
		Bestand.setBackground(Constants.COLOR21);
		Bestand.setForeground(Constants.COLOR15);
		Bestand.setUI(new BasicMenuUI());
		Bewerken.setBackground(Constants.COLOR21);
		Bewerken.setForeground(Constants.COLOR15);
		Bewerken.setUI(new BasicMenuUI());
		bar.setOpaque(true);
		bar.setUI(new BasicMenuBarUI());
		bar.add(Bestand);
		Bestand.add(new JMenuItem(new SubdomeinAction()));
		Bestand.add(new JMenuItem(new LeerdoelAction()));
		Bestand.addSeparator();

		ExportPanel exporter = new ExportPanel() {

			@Override
			public DomStudentModelStructure getModel() {
				return getTreeModel();
			}

			@Override
			public Component asComponent() {
				return LeerdomeinEditPanel2.this.asComponent();
			}
			
			@Override public void  save(DomStudentModelStructure model) {
				setModel0(model);
			}
		};

		Bestand.add(new JMenuItem(new ExportAction(exporter)));
		Bestand.add(new JMenuItem(new ImportAction(this)));
		bar.add(Bewerken);
		Bewerken.add(new JMenuItem(new Knippen()));
		Bewerken.add(new JMenuItem(new Kopieren()));
		Bewerken.add(new JMenuItem(new Plakken()));
		// Bewerken.add(new JMenuItem(new Wijzigen()));
		Bewerken.add(new JMenuItem(new Omhoog()));
		Bewerken.add(new JMenuItem(new Omlaag()));
		Bewerken.add(new JMenuItem(new Verwijderen()));
		//if (DwoHelper.isTest())
		  bar.add(Instellingen);
		Instellingen.setBackground(Constants.COLOR21);
		Instellingen.setForeground(Constants.COLOR15);
		Instellingen.setUI(new BasicMenuUI());
		Instellingen.add(new JMenuItem(new MethodeAction()));
		StandardAction action = new StandardAction();
        if (action.isEnabled())
          Instellingen.add(new JMenuItem(action));
		CreateVariantAction cva = new CreateVariantAction();
		Instellingen.add(new JMenuItem(cva));
		RenameVariantAction rva = new RenameVariantAction();
		Instellingen.add(new JMenuItem(rva));
		RemoveVariantAction dva = new RemoveVariantAction();
		Instellingen.add(new JMenuItem(dva));
		
		/* Hack voor peter.
		 * Instellingen.addSeparator(); Instellingen.add(new NoMethodAction(exporter));
		 */		
		
		bar.add(Box.createHorizontalGlue());

		add(split, BorderLayout.CENTER);
		String locale = JComponent.getDefaultLocale().getLanguage();
		NodeVector v = new NodeVector(locale);
		v.setTitle("Leerdomein");
		root = new InvisibleNode(v, true, true);
		model = new InvisibleTreeModel(root);
		tree = new JTree(model);
        graph = new EditableGraph();
        graph.addActionListener(new GraphTreeAction(tree));
        graph.addActionListener(this::graphActionPerformed);

		TreeCellRenderer renderer = new TreeCellRenderer();
		renderer.updateUI();
		tree.setCellRenderer(renderer);
		tree.updateUI();
		tree.setDragEnabled(true);
		tree.setTransferHandler(new TreeTransferHandler());
		leftBox = new JPanel(new BorderLayout());
		leftBox.setBorder(BorderFactory.createLineBorder(Constants.COLOR13));
		leftBox.add(bar, BorderLayout.NORTH);
		JScrollPane scrollpane = new JScrollPane(tree);
		scrollpane.setViewportBorder(BorderFactory.createEmptyBorder());
		scrollpane.setBorder(BorderFactory.createEmptyBorder());
		pref = scrollpane.getPreferredSize();
		pref.width = Math.max(580, pref.width); // 580 wide.
		scrollpane.setPreferredSize(pref);

		leftBox.add(scrollpane, BorderLayout.CENTER);
		leftSouth = Box.createVerticalBox();
		filterAction = new FilterAction(this, this::filter);
        JButton filterBtn = new JButton(filterAction);
        methodBox = new JCheckBox("Methode-indeling");
        methodBox.setFont(font);
        methodBox.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 4));
        methodBox.setHorizontalAlignment(SwingConstants.LEFT);
        methodSelect = new JComboBox<DomMethod>(MethodsProperties.instance());
        methodListener = new MethodListener(methodBox, tree, filterAction);
        graph.addActionListener(methodListener);
        methodSelect.addItemListener(this);
        methodSelect.setAlignmentX(0);
        leftSouth.add(methodBox);
        Box hb = Box.createHorizontalBox();
        hb.setAlignmentX(0);
        JLabel l = new JLabel("Actieve methode");
        l.setForeground(Constants.COLOR15);
        l.setFont(font);
        hb.add(l);
        hb.add(Box.createHorizontalStrut(16));
        hb.add(methodSelect);
		hb.add(Box.createHorizontalGlue());
		hb.add(filterBtn);
		hb.add(Box.createHorizontalGlue());
		leftSouth.add(hb);
		leftSouth.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		leftBox.add(leftSouth, BorderLayout.SOUTH);
		
	    JSplitPane splitLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	    splitLeft.setBorder(BorderFactory.createEmptyBorder());
	    splitLeft.setResizeWeight(0.9);
	    BasicSplitPaneUI suiLeft = (BasicSplitPaneUI) BasicSplitPaneUI.createUI(splitLeft);
	    splitLeft.setUI(suiLeft);
	    BasicSplitPaneDivider dividerLeft = sui.getDivider();
	    dividerLeft.setBorder(BorderFactory.createEmptyBorder());
	    dividerLeft.setBackground(Constants.COLOR20);
	    splitLeft.setDividerSize(0);
		
	    splitLeft.setTopComponent(leftBox);
		
		split.setLeftComponent(splitLeft);

		JPanel rightBox = new JPanel(new BorderLayout());
		subtitle = new JTextField();
		subtitle.setFont(font.deriveFont(14f));
		container = new JPanel(new GridLayout(1, 1));
		container.setPreferredSize(new Dimension(500, 325));
// variant next to subtitle
		Box rightNorth = Box.createHorizontalBox();
		rightNorth.add(subtitle);
		rightNorth.add(Box.createHorizontalGlue());
		MutableComboBoxModel<DomStudentModelVariant> varianten = new DefaultComboBoxModel<DomStudentModelVariant>();
		variantBox = new JComboBox<>(varianten);
		variantBox.setBackground(Constants.COLOR13);
		variantBox.setForeground(Color.white);
		Dimension size = variantBox.getPreferredSize();
		size.width += 40;
		variantBox.setMinimumSize(size);
		variantBox.setPreferredSize(size);

		variantBox.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				boolean on = variantBox.getSelectedIndex() > 0;
				dva.setEnabled(on);
				rva.setEnabled(on);
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				dva.setEnabled(false);
				rva.setEnabled(false);
			}			
		});
		variantBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean on = variantBox.getSelectedIndex() > 0;
				dva.setEnabled(on);
				rva.setEnabled(on);
			}
		});

		rightNorth.add(variantBox);
		rightBox.add(rightNorth, BorderLayout.NORTH);
		rightBox.add(container, BorderLayout.CENTER);

		settingsRO = Box.createHorizontalBox();
		settingsRO.setOpaque(true);
		settingsRO.setBackground(Constants.COLOR20);
		JButton voorkennisRO = new JButton(new VoorkennisAction(true, this, tree, graph));
		voorkennisRO.setFont(font);
		voorkennisRO.setPreferredSize(new Dimension(120, 24));
		settingsRO.add(voorkennisRO);
		settingsRO.add(Box.createHorizontalGlue());
//		AnyMethodAction methodeAction3 = new AnyMethodAction(true, this, tree);
        methodeAction4 = new AnyMethodAction(true, this, tree);
//		methodeAction3.setMethode(MethodsProperties.instance().get(1));
        methodeAction4.init(0);
		
//        JButton genrRO = new JButton(methodeAction3);
//		genrRO.setFont(font);
//		genrRO.setPreferredSize(new Dimension(140, 24));
//		settingsRO.add(genrRO);
//		settingsRO.add(Box.createHorizontalStrut(10));
		JButton mwRO = new JButton(methodeAction4);
		mwRO.setFont(font);
		mwRO.setPreferredSize(new Dimension(140, 24));
		settingsRO.add(mwRO);
		settingsRO.setBorder(BorderFactory.createEmptyBorder(10, 10, 8, 10));

		settings = settingsRW = new JPanel(null);
		settings.setLayout(new BoxLayout(settings, BoxLayout.PAGE_AXIS));
		Border inner = BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Constants.COLOR13),
				"Instellingen", TitledBorder.CENTER, TitledBorder.CENTER, new Font("SansSerif", Font.BOLD, 11),
				Constants.COLOR13);
		Border outer = BorderFactory.createEmptyBorder(10, 10, 8, 10);
		settings.setBorder(BorderFactory.createCompoundBorder(outer, inner));

		Box bkt = Box.createHorizontalBox();
		voorkennisActionRW = new VoorkennisAction(false, this, tree, graph);
		deselectionsActionRW = new DeselectionAction(false, this, tree, graph);
		voorkennis = new JButton(voorkennisActionRW);
		voorkennis.setFont(font);
		voorkennis.setPreferredSize(new Dimension(120, 20));
		bkt.add(voorkennis);
		bkt.add(Box.createHorizontalGlue());
		JLabel nodeSizeLabel = new JLabel("Node size: ");
		nodeSizeLabel.setForeground(Constants.COLOR15);
		bkt.add(nodeSizeLabel);
		nodeSizeChoice = new JComboBox<>();
		nodeSizeChoice.setPreferredSize(new Dimension(50, 20));
		nodeSizeChoice.setMaximumSize(new Dimension(50, 20));
		nodeSizeChoice.addItem(DEFAULT_NODE_SIZE);
		nodeSizeChoice.addItem(48);
		nodeSizeChoice.addItem(72);
		nodeSizeChoice.addItem(96);
		bkt.add(nodeSizeChoice);
		settings.add(bkt);
		settings.add(Box.createVerticalStrut(10));

		bkt = Box.createHorizontalBox();
		JLabel parametersLabel = new JLabel("Knowledge tracing parameters:");
		parametersLabel.setForeground(Constants.COLOR15);
		bkt.add(parametersLabel);
		bkt.add(Box.createHorizontalGlue());
		bkt.add(l = new JLabel("Init "));
		l.setForeground(Constants.COLOR15);
		init = new JFormattedTextField(NumberFormat.getInstance());
		init.setPreferredSize(new Dimension(50, 20));
		init.setMaximumSize(new Dimension(50, 24));
		bkt.add(init);
		bkt.add(l = new JLabel(" Learn "));
		l.setForeground(Constants.COLOR15);
		learn = new JFormattedTextField(NumberFormat.getInstance());
		learn.setPreferredSize(new Dimension(50, 20));
		learn.setMaximumSize(new Dimension(50, 24));
		bkt.add(learn);
		bkt.add(l = new JLabel(" Slip "));
		l.setForeground(Constants.COLOR15);
		slip = new JFormattedTextField(NumberFormat.getInstance());
		slip.setPreferredSize(new Dimension(50, 20));
		slip.setMaximumSize(new Dimension(50, 24));
		bkt.add(slip);
		settings.add(bkt);
		settings.add(Box.createVerticalStrut(10));

		bkt = Box.createHorizontalBox();
		parametersLabel = new JLabel("Koppeling lesmateriaal:");
		parametersLabel.setForeground(Constants.COLOR15);
		bkt.add(parametersLabel);
		bkt.add(Box.createHorizontalGlue());
		methodeAction2 = new AnyMethodAction(this, tree);
//        AnyMethodAction methodeAction = new AnyMethodAction(this, tree);
//		methodeAction.setMethode(MethodsProperties.instance().get(1));
		methodeAction2.setMethode(MethodsProperties.instance().get(0));
		
//        JButton genr = new JButton(methodeAction);
//.setFont(font);
//		genr.setPreferredSize(new Dimension(140, 20));
//		bkt.add(genr);
//		bkt.add(Box.createHorizontalStrut(10));
		JButton mw = new JButton(methodeAction2);
		mw.setFont(font);
		mw.setPreferredSize(new Dimension(140, 20));
		bkt.add(mw);

		settings.add(bkt);

		rightBox.add(settings = settingsRO, BorderLayout.SOUTH);
		rightBox.setBorder(BorderFactory.createLineBorder(Constants.COLOR13));
		// JTabbedPane tabs = new JTabbedPane(JTabbedPane.BOTTOM);
		// tabs.addTab("Item", rightBox);

		// tabs.addTab("Voorkennisgraaf", graph);
		split.setRightComponent(rightBox);

		JTextField leerdoelTitelEditor = subtitle;
		leerdoelTitelEditor.setForeground(Color.WHITE);
		leerdoelTitelEditor.setBackground(Constants.COLOR13);
		// leerdoelTitelEditor.setMaximumSize(new Dimension(800,30));
		leerdoelTitelEditor.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 20));
		leerdoelTitelEditor.setFont(leerdoelTitelEditor.getFont().deriveFont(Font.BOLD, 14));
		leerdoelTitelEditor.setOpaque(true);

		tree.addTreeSelectionListener(this);
		// tabs.addChangeListener(this);

		graphButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ("Graph".equals(graphButton.getText())) {
					graphButton.setText("Hide Graph");
					split.setResizeWeight(0);
					split.setRightComponent(graph);
					Dimension pref = scrollpane.getPreferredSize();
					pref.width = 380;
					leftBox.setPreferredSize(pref);
					graph.setPreferredSize(new Dimension(1000, 650));
					graph.setModel(tree, null, activeMethod);
					
					splitLeft.setBottomComponent(rightBox);
					splitLeft.setDividerSize(20);
					packWindow();
				} else {
					graphButton.setText("Graph");
					split.setResizeWeight(0.5);
					Dimension pref = scrollpane.getPreferredSize();
					pref.width = 580;
					leftBox.setPreferredSize(pref);
					split.setRightComponent(rightBox);
					splitLeft.setDividerSize(0);
					if(editable)
					  graph.updateModel(model);
					packWindow();
				}

			}
		});
		
		variantBox.addItemListener(new VariantListener());
	}
	
    private void packWindow() {
		((Window) SwingUtilities.getAncestorOfClass(Window.class, this)).pack();
	}

	private boolean aquireLock() {
		if (!lock) {
			if (prop.getCurrent().getPublishState() == PublishState.edit) {
				DomStudentModelContext current = prop.getCurrent();
				Long version = current.getOptLock();
				try {
					current = prop.getModel(prop.getCurrent());
				} catch (Dwo2Exception e) {
					LOG.log(Level.SEVERE, "refresh model", e);
					GuiCreator.instance().ShowErrorDialog(this, e);
					return false;
				}
				if (!current.getOptLock().equals(version)) {
					setModel(current.getModelStructure(), current.getPublishState());
				}
				if (current.getPublishState() == PublishState.edit) {

					String msg = "Weet je zeker dat wilt bewerken?" + "\n";
					if (structure.getOwner() != null) {
						DateFormat dateFormat = DateFormat.getDateTimeInstance();
						msg += structure.getOwner() + " is vanaf "
								+ dateFormat.format(new Date(structure.getTimestamp().longValue())) + " bezig";
					}
					int ok = JOptionPane.showConfirmDialog(this, msg, "", JOptionPane.WARNING_MESSAGE);
					if (ok != JOptionPane.OK_OPTION)
						return false;
				}
			}
			prop.getCurrent().setPublishState(PublishState.edit);
			structure.setOwner(DwoHelper.getCurrentUser().getUniqueDisplayName());
			structure.setTimestamp(System.currentTimeMillis());
			try {
				prop.updateModel(structure);
				lock = true;
			} catch (Dwo2Exception e) {
				if (e.getDwo2Code() == Dwo2ExceptionCode.Rest_ObjectModified)
					return aquireLock();
				GuiCreator.instance().ShowErrorDialog(this, e);
				return false;
			}
		}
		return true;
	}

	private String getTitle() {
		if (structure != null) {
			return NodeVector.getTitle(structure.getInfo().getTitle(),getLocale().getLanguage());
		}
		return "Leerdomein";
	}

	private boolean editable;
	private WiskOpdrEditPanel wiskOpdrEditPanel;
	private JPanel container;

	private Box leftSouth;

	private JPanel leftBox;
	private boolean lock;

	public void setEditAndLock() {
	  lock = true;
	  setEditable(true);
	}
	
	
	public void setEditable(boolean b) {
		editable = b;
		Container parent = settings.getParent();
		parent.remove(settings);
		boolean visible = settings.isVisible();
		methodListener.setEditable(b);
		if (b) {
			settings = settingsRW;
		} else {
			settings = settingsRO;
		}
		parent.add(settings, BorderLayout.SOUTH);
		settings.setVisible(visible);
		//leftSouth.setVisible(!b);
		fillSelection();
		if (b) {
			//filter(Collections.emptyMap());
			leftBox.setBorder(BorderFactory.createLineBorder(Constants.COLOR13));
			bewerken.setText(TextMapper.getText(TextMapper.GUIH_STOP_EDIT));
			title.setText("Editor " + getTitle());
			bar.show();
			south.show();
			subtitle.setEditable(true);
			((DefaultTreeCellRenderer) tree.getCellRenderer()).setBackgroundNonSelectionColor(Color.WHITE);
			tree.setBackground(Color.WHITE);
		} else {
			leftBox.setBorder(BorderFactory.createLineBorder(Constants.COLOR20));
			bewerken.setText(TextMapper.getText(TextMapper.GUIH_EDIT));
			title.setText(getTitle());
			bar.hide();
			south.hide();
			subtitle.setEditable(false);
			((DefaultTreeCellRenderer) tree.getCellRenderer()).setBackgroundNonSelectionColor(Constants.COLOR20);
			tree.setBackground(Constants.COLOR20);
		}
		graph.setEditMode(b);
	}

	public void setModel(DomStudentModelStructure model, PublishState ps) {
		lock = false;
		bewerken.setEnabled(ps != PublishState.overt);
        filterAction.unset();
		setModel0(model);
		resultModel = null;
	}


// move to DomStudentModelStructure	
	private static void addKeySet(DomStudentModelObj obj, Set<String> keySet) {
	        if (obj.getObjectives() != null) 
	            for (DomStudentModelObj o : obj.getObjectives()) addKeySet(o, keySet);
	        else {
	          try { 
	            Set<String> keys = obj.getInfo().getMethods().keySet(); // expect NPE's
	            keySet.addAll(keys);
	          } catch (Exception oops) {}
	        }
	    }

	public static List<String> cacheMethods(DomStudentModelStructure src) {
        DomStudentModelStructure structure = src;
        Set<String> keySet = new HashSet<>();
        keySet.add(DomMethod.key(structure.getActiveMethod()));
        for (DomStudentModelCategory cat: structure.getCategories()) {
            for (DomStudentModelObj obj: cat.getObjectives()) {
                addKeySet(obj, keySet);
            }
        }
        keySet.remove(null);
        ArrayList<String> result = new ArrayList<String>(keySet);
        Collections.sort(result); // vaste volgorde!
        return result;
    }
	
  public void initMethodSelect(DomStudentModelStructure model) {
    koppeling = model.getMethods();
    List<String> k = koppeling; if (k == null) k = cacheMethods(model);
    initMethodSelect(k);
  }

  public void initMethodSelect(List<String> koppeling) {
    Vector<DomMethod> methods = MethodsProperties.instance();
    Object selected = MethodsProperties.instance().getMethod(activeMethod);
    if (koppeling != null) {
      methods =
          methods.stream().filter(t -> t == selected || t.key() == null || koppeling.contains(t.key()))
              .collect(Vector::new, Vector::add, Vector::addAll);
    }
    DefaultComboBoxModel<DomMethod> selectmodel;
    selectmodel = new DefaultComboBoxModel<>(methods);
    methodSelect.setModel(selectmodel);
    methodSelect.setSelectedItem(selected);
  }

	private void setModel0(DomStudentModelStructure model) {
		String locale = getLocale().getLanguage();
		if (model == null) {
			model = new DomStudentModelStructure();
			model.setInfo(new DomStudentModelContextInfo(new TreeMap<>(), new TreeMap<>()));
			model.getInfo().getTitle().put(locale, "Model");
			model.getInfo().getDescription().put(locale, "");
			model.setCategories(new ArrayList<>());
			lock = true;
		}
		this.structure = model;
		NodeVector vector = new NodeVector(model.getCategories(), model.getInfo(), locale);
		this.model.setRoot(root = new InvisibleNode(vector, true, true));
		insert(vector, root);
		this.subtitle.setText("");
		this.variantBox.setModel(new DefaultComboBoxModel<DomStudentModelVariant>());
		// text.setEditable(false);
		// OPSLAAN_ACTION.setDescription("");
		this.model.nodeStructureChanged(root);
		this.structure = model;
		setEditable(editable);
		// OPSLAAN_ACTION.left();
        setActiveMethod(model.getActiveMethod());
        initMethodSelect(structure);
		graph.setModel(this.model,null, activeMethod);
		filterAction.doFilter();
	}

  protected void setActiveMethod(PersistenceId am) {
    activeMethod = am;
    methodListener.setActiveMethod(am);
    methodSelect.setSelectedItem(MethodsProperties.instance().getMethod(am));
	filterAction.setActiveMethod(am);
	methodeAction4.setMethode(am);
	methodeAction2.setMethode(am);
	if (graph.isShowing()) {
      graph.setModel(this.model,null,activeMethod);
	}
    filterAction.doFilter();    
  }

	static void insert(NodeVector vector, InvisibleNode node) {
      for(Object child: vector) {
        if (child instanceof NodeVector) {
          InvisibleNode parent = new InvisibleNode(child);
          insert((NodeVector)child, parent);
          node.add(parent);
        } else {
          node.add(new InvisibleNode(child, false, true));
        }
      }
    
  }

  DomStudentModelStructure resultModel;

	static DomStudentModelStructure untitled;
	static DomStudentModelCategory untitledCategory;
	static DomStudentModelObj untitledObjective;
  private FilterAction filterAction;
  private AnyMethodAction methodeAction4;
  private AnyMethodAction methodeAction2;
  private JCheckBox methodBox;
  private MethodListener methodListener;
  private JComboBox<DomStudentModelVariant> variantBox;

	static {
		Genson genson = new GensonBuilder().create();
		URL root = DwoHelper.getResourceUrlPath();
		try {
			URL content = new URL(root, "resources/untitled-learning-domain.json");
			InputStream input = content.openStream();
			untitled = genson.deserialize(input, DomStudentModelStructure.class);
			input.close();
			untitledCategory = untitled.getCategories().get(0);
			untitledObjective = untitledCategory.getObjectives().get(0);
			untitledObjective.getInfo().setId(null); // clear id
		} catch (IOException e) {

		}
	}

	public DomStudentModelStructure getModel() {
		return resultModel;
	}

	private DomStudentModelStructure getTreeModel() {
		DomStudentModelStructure result = new DomStudentModelStructure();
		Node u;
		u = (Node) root.getUserObject();
		result.setInfo(u.getInfo());
		List<DomStudentModelCategory> categories = new ArrayList<>(root.getChildCount());
		result.setCategories(categories);
		Enumeration<TreeNode> children = root.children();
		while (children.hasMoreElements()) {
			InvisibleNode object = (InvisibleNode) children.nextElement();
			u = (Node) object.getUserObject();
			DomStudentModelCategory cat = new DomStudentModelCategory();
			cat.setInfo(u.getInfo());
			List<DomStudentModelObj> objectives = new ArrayList<>(object.getChildCount());
			cat.setObjectives(objectives);
			Enumeration<TreeNode> kids = object.children();
			while (kids.hasMoreElements()) {
				InvisibleNode kid = (InvisibleNode) kids.nextElement();
				u = (Node) kid.getUserObject();
				DomStudentModelObj objective = new DomStudentModelObj();
				objective.setInfo(u.getInfo());
				objectives.add(objective);
				if (kid.getAllowsChildren()) {
					setObjectiveChildren(objective, kid.getChildCount(), kid.children());
				}
			}
			categories.add(cat);
		}
		result.setCategories(categories);
		result.setActiveMethod(activeMethod);
		result.setMethods(koppeling);
		return result;
	}

	private void setObjectiveChildren(DomStudentModelObj node, int childCount,
			Enumeration<? extends TreeNode> children) {
		List<DomStudentModelObj> objectives = new ArrayList<>(childCount);
		node.setObjectives(objectives);
		while (children.hasMoreElements()) {
			InvisibleNode kid = (InvisibleNode) children.nextElement();
			Node u = (Node) kid.getUserObject();
			DomStudentModelObj objective = new DomStudentModelObj();
			objective.setInfo(u.getInfo());
			objectives.add(objective);
			if (kid.getAllowsChildren()) {
				setObjectiveChildren(objective, kid.getChildCount(), kid.children());
			}

		}

	}

	public JButton ok() {
		return okButton;
	}

	void fillSelection() {
		TreePath path = tree.getSelectionPath();
		if (path == null) {
			subtitle.setText("");
			variantBox.setModel(new DefaultComboBoxModel<DomStudentModelVariant>());
			setDescription("");
			settings.setVisible(false);
			variantBox.setVisible(false);
			return;
		}
		InvisibleNode node = (InvisibleNode) path.getLastPathComponent();
		Object u = node.getUserObject();
		subtitle.setText(u.toString());
		setDescription(u);
		if (u instanceof NodeLeaf) {
			DomStudentModelContextInfo info = ((NodeLeaf) u).getInfo();
			Double d = info.getSlip();
			if (d == null)
				d = 0.05; // DEFAULT SLIP
			slip.setValue(d);
			d = info.getInit();
			if (d == null)
				d = 0.5; // DEFAULT INIT;
			init.setValue(d);
			d = info.getLearn();
			if (d == null)
				d = 0.2; // DEFAULT LEARN;
			learn.setValue(d);
			settings.setVisible(true);
			Integer ns = info.getNodeSize();
			if (ns == null) ns = DEFAULT_NODE_SIZE;
			nodeSizeChoice.setSelectedItem(ns);
		    MutableComboBoxModel<DomStudentModelVariant> m = new DefaultComboBoxModel<DomStudentModelVariant>();
			if (info.getVariants() != null) { 
				((NodeLeaf) u).getVariants().add(new DomStudentModelVariant()); // only if not there already
				info.getVariants().forEach(m::addElement);
				
			} 
			variantBox.setModel(m);
			DomStudentModelVariant variant = ((NodeLeaf) u).getVariant();
			variantBox.setSelectedItem(variant);
			variantBox.setVisible(m.getSize()>1);
			javax.swing.Action action = deselectionsActionRW;
			if (variant == null || variant.getName() == null) action = voorkennisActionRW;
			voorkennis.setAction(action);
			
		} else {
			settings.setVisible(false);
			variantBox.setVisible(false);
		}
	}

	private String updateDescription(String description, Map<String,Boolean> layers) {
		Object o = StringCodeObject.decodeStringToObject(description,wo);
		Map m = (Map)o;
		Object oo = m.get("instellingen");
		if (oo instanceof String) oo = StringCodeObject.decodeStringToObject(oo.toString(),wo);
		Map mm = (Map) oo;
		String[] layerNames = (String[]) mm.get("layerNames");
		if (layerNames != null) { 
			boolean[] layerVisible = (boolean[]) mm.get("layerVisible");
			for(Map.Entry<String, Boolean> entry: layers.entrySet()) 
				setLayer(entry.getKey(), entry.getValue(), layerNames, layerVisible);
			oo = StringCodeObject.encodeObjectToString(mm);
			m.put("instellingen", oo);
			description = StringCodeObject.encodeObjectToString(o);
		}
		return description;
	}
	
	private void setLayer(String key, Boolean value, String[] names, boolean[] values) {
		for (int i = 0; i < names.length; i++) {
			if (key.equals(names[i]))
				values[i] = value;
		}
	}
	
	ClassLoader wo;
	private JButton voorkennis;
	private VoorkennisAction voorkennisActionRW;
	{
		try {
			wo = WiskOpdrCache.getInstance().getClassLoader();
		} catch(Throwable oops) {}
	}
	
	private Map<String,Boolean> getLayers(String description) {
		Object o = StringCodeObject.decodeStringToObject(description, wo);
		Map m = (Map)o;
		Object oo = m.get("instellingen");
		if (oo instanceof String) oo = StringCodeObject.decodeStringToObject(oo.toString(), wo);
		m = (Map) oo;
		Map<String,Boolean> result = new TreeMap<>();
		String[] layerNames = (String[]) m.get("layerNames");
		if (layerNames != null) {
			boolean[] layerVisible = (boolean[]) m.get("layerVisible");
			for(int i = 0; i < layerNames.length; i++) {
				result.put((String) layerNames[i], layerVisible[i]);
			}
		}
		return result;
	}
	
	
	
	
	void setDescription(Object u) {
		if (u instanceof Node) {
			String description = ((Node) u).getDescription();
			if (description == null || description.startsWith(WISKOPDR_SIG) || description.isEmpty()) {

				// wat staat er 
				if (u instanceof NodeLeaf) {
					NodeLeaf nl = (NodeLeaf) u;
					DomStudentModelVariant v = nl.getVariant();
					if (v != null) {
						description = updateDescription(description, v.getLayers());
					}					
				}
				
				
				if (editable) {
					Locale locale = getLocale();
					wiskOpdrEditPanel = WiskOpdr.getWiskOpdrEditPanel(description, locale, container.getWidth(),
							container.getHeight(), 425, 300);
					wiskOpdrEditPanel.setBackground(Color.WHITE);
					container.removeAll();
					container.add(wiskOpdrEditPanel);
					wiskOpdrEditPanel.setRequestFocusEnabled(true);
					wiskOpdrEditPanel.setFocusable(true);
					wiskOpdrEditPanel.requestFocusInWindow();
				} else {
					AppletStub stub = new DummyStub();
					WiskOpdrPanel panel = WiskOpdr.getWiskOpdrPanel(description, getLocale(), stub);
					panel.setBackground(Color.WHITE);
					JScrollPane pane = new JScrollPane(panel);
					pane.setBorder(BorderFactory.createEmptyBorder());
					pane.setViewportBorder(BorderFactory.createEmptyBorder());
					pane.setBackground(Color.WHITE);
					pane.getViewport().setBackground(Color.WHITE);
					container.removeAll();
					container.add(pane);
				}
			} else {
				wiskOpdrEditPanel = null;
				container.removeAll();
			}
		} else {
			wiskOpdrEditPanel = null;
			container.removeAll();
		}
	}

	private String toJSON(String string) {
		StringWriter writer = new StringWriter();
		try {
			Hashtable map = (Hashtable) StringCodeObject.decodeStringToObject(string,
					WiskOpdrCache.getInstance().getClassLoader());
			JSONEncoder.encode(map, writer, null);
		} catch (Exception e) {
			// LOG.log(Level.WARNING, "toJSON", e);
		}
		return writer.toString();

	}

	private void commitEdit(JFormattedTextField field) {
		try {
			field.commitEdit();
		} catch (ParseException e) {
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (e.isAddedPath()) {
			if (editable) {
				TreePath path = e.getOldLeadSelectionPath();
				safeSelection(path);
			}
			fillSelection();
			validate();
		}
	}

	public void safeSelection(TreePath path) {
		if (path != null) {
			InvisibleNode node = (InvisibleNode) path.getLastPathComponent();
			Object u = node.getUserObject();
			String string = subtitle.getText();
			if (u instanceof Node) {
				Node n = (Node) u;
				n.setTitle(string);
				model.nodeChanged(node);
				String description = wiskOpdrEditPanel == null ? n.getDescription() : wiskOpdrEditPanel.getText();
				if (u instanceof NodeLeaf) {
					DomStudentModelVariant v = ((NodeLeaf) u).getVariant();
					if (v != null) {
						v.setLayers(getLayers(description));
					}
				}
				n.setDescription(description);
				n.setDescriptionAsJSON(toJSON(description)); // could be lazy...
			}
			if (u instanceof NodeLeaf) {
				NodeLeaf n = (NodeLeaf) u;
				commitEdit(init);
				commitEdit(learn);
				commitEdit(slip);
				n.setInit((Double) init.getValue());
				n.setLearn((Double) learn.getValue());
				n.setSlip((Double) slip.getValue());
				n.setNodeSize((Integer) nodeSizeChoice.getSelectedItem());
				n.setVariant(n.getVariant());
			}
		}
	}

	@Override
	public Component asComponent() {
		return this;
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		closeWindow((ConfirmDialog) e.getWindow());
	}

	private void opslaanAction(ActionEvent e) {
		safeSelection(tree.getSelectionPath());
		if (editable && graph.isShowing())
			graph.updateModel(model);// voorkennis en x,y
		resultModel = getTreeModel();
		resultModel.setOwner(DwoHelper.getCurrentUser().getUniqueDisplayName());
		resultModel.setTimestamp(System.currentTimeMillis());
		structure = resultModel;
		try {
			prop.updateModel(resultModel);
		} catch (Dwo2Exception e1) {
			Dwo2ExceptionCode code = e1.getDwo2Code();
			LOG.log(Level.SEVERE, "opslaanAction", e1);
			GuiCreator.instance().ShowErrorDialog(this, e1);
		}
		// setEditable(false);
	}

	// private void cancelAction(ActionEvent e) {
	// setEditable(false);
	// }

	private void closeWindow(ConfirmDialog window) {
		if (!Objects.equals(activeMethod,structure.getActiveMethod()) && !editable) {
			try {
				int option = confirm();
				switch (option) {
				case JOptionPane.CANCEL_OPTION:
					return;
				case JOptionPane.YES_OPTION:
					structure.setActiveMethod(activeMethod);
					prop.updateActiveMethod(structure);
					if(resultModel != null) 
					{   resultModel.setActiveMethod(activeMethod);
						window.ok(null);
						return;
					}
				case JOptionPane.NO_OPTION:
					window.cancel(null);
				}
				return;
			} catch (Dwo2Exception e) {
			}
		}
		
		
		if (editable || !Objects.equals(activeMethod,structure.getActiveMethod()) ) {
			int option = confirm();
			switch (option) {
			case JOptionPane.CANCEL_OPTION:
				return;
			case JOptionPane.YES_OPTION:
				opslaanAction(null);
				window.ok(null);
				return;
			case JOptionPane.NO_OPTION:
			}
		}
		if (resultModel != null) {
			window.ok(null);
		} else
			window.cancel(null);
	}

	private int confirm() {
		safeSelection(tree.getSelectionPath());
// if graph is visible?
		if (graph.isShowing())
		  graph.updateModel(tree.getModel());
		DomStudentModelStructure toSafe = getTreeModel();
		if (toSafe.same(structure) && prop.getCurrent() != null)
			return JOptionPane.NO_OPTION; // no need to safe.

		return JOptionPane.showConfirmDialog(this, okButton.getText(), "", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	public void importModel(DomStudentModelStructure model) {
		DomStudentModelStructure tmp = structure;
		filterAction.unset();
		setModel0(model);
		structure = tmp;
	}

	public boolean isLock() {
		return lock;
	}

	private void graphActionPerformed(ActionEvent ev) {
	  if("filter".equals(ev.getActionCommand()) && graph.isShowing()) {
	    Set<String> visible = graph.getVisibleNodes(); // id's of visible nodes
        model.activateFilter(!visible.isEmpty());
        filterAction.setFilter(graph.getFilter());
        model.setRoot(filter(root, visible));
        model.nodeStructureChanged((TreeNode) model.getRoot());
	  }
	}

    static InvisibleNode filter(InvisibleNode parent, Set<String> visible) {
      InvisibleNode node;
      if (!(parent instanceof InvisibleNode)) {
          node = new InvisibleNode(parent.getUserObject());
          node.setAllowsChildren(parent.getAllowsChildren());
          Enumeration<?> children = parent.children();
          while (children.hasMoreElements()) {
              InvisibleNode object = (InvisibleNode) children.nextElement();
              node.add(filter(object, visible));
          }
      } else {
          node = (InvisibleNode) parent;
          @SuppressWarnings("unchecked")
          Enumeration<InvisibleNode> children = (Enumeration) node.children();
          while (children.hasMoreElements()) {
              InvisibleNode object = children.nextElement();
              filter(object, visible);
          }
      }
      if (node.isLeaf() && !node.getAllowsChildren()) {
          NodeLeaf leaf = (NodeLeaf) node.getUserObject();
          String id = leaf.getId();
          node.setVisible(visible.contains(id));
      } else {
          int cnt = node.getChildCount(true);
          node.setVisible(cnt != 0);
      }

      return node;
    }

    public void end() {
      graph.end();
      root = null;
      structure = null;
      model.setRoot(null);
      methodListener.end();
      container.removeAll();
      long voor = Runtime.getRuntime().freeMemory();
      System.gc();
      long na = Runtime.getRuntime().freeMemory();
      LOG.log(Level.INFO, "voor {0}, na {1}, diff {2}", new Object[] {voor, na, na-voor});
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
      if (e.getSource() == methodSelect) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          DomMethod m = (DomMethod) e.getItem();
          if (m != null && !Objects.equals(m.getId(),activeMethod)) {
            setActiveMethod(m.getId());
          }
        }
      }
      
    }

	@Override
	public void save(DomStudentModelStructure model) {
		// push model in tree
		
	}

}
