package My.Vaadin.example.app;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

/*
 * Datasource: options: BeanItemContainer, which takes a list of collection.
 * 
 * This is an Vaadin Application that demostrate Grid and ComboBox that bind with the same datasource
 * When Click the Item in the Grid and click delete button, it will remove the item from the datasource
 * and update the content of Grid and ComboBox automatically. 
 * 
 * The TextField component that takes the search input from the user and 
 * modify the datasource accordingly. When the reset button is pressed, the datasource is reset to it's orginally state.
 *
 * The list of collection did not modified onces the datasource item is removed.
 * 
 */
@Theme("mytheme")
@Widgetset("My.Vaadin.example.app.MyAppWidgetset")
public class MyUI extends UI {

	List<ProductOption> list= new ArrayList<ProductOption>();
	BeanItemContainer<ProductOption> options;
	MGrid<ProductOption> myGrid ;
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	
    	setTempFile();
    	
        final MVerticalLayout layout = new MVerticalLayout();
      
        myGrid = new MGrid<ProductOption>();
        myGrid.setWidth("50%");
        myGrid.setSelectionMode(SelectionMode.SINGLE);
        myGrid.setEditorEnabled(true);
       
        
        
        MButton button = new MButton("Remove Item");
        button.addClickListener(this::removeList);
        
        options = new BeanItemContainer<ProductOption>(ProductOption.class);
        myGrid.setContainerDataSource(options);
        myGrid.setColumns("optionName");
        //After setContainerDateSource with the Grid, we can get the EditorFieldGroup and its CommitHandler to save the change back to the list.
        myGrid.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
			
        	
			@Override
			public void preCommit(CommitEvent commitEvent) throws CommitException {
				
				BeanItem<ProductOption> p=(BeanItem<ProductOption>) commitEvent.getFieldBinder().getItemDataSource();
				ProductOption pOption=p.getBean();
				list.remove(pOption);
				
			}
			
			@Override
			public void postCommit(CommitEvent commitEvent) throws CommitException {
	
				BeanItem<ProductOption> p=(BeanItem<ProductOption>) commitEvent.getFieldBinder().getItemDataSource();
				ProductOption pOption=p.getBean();
				list.add(pOption);
				
				list.stream().forEach(x->System.out.println(x.getOptionName()));
				
			}
		});
        
        
        
        options.addAll(list);
        
        ComboBox comboBox = new ComboBox();
        comboBox.setContainerDataSource(options);
        comboBox.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        comboBox.setItemCaptionPropertyId("optionName");
        
        
        comboBox.addValueChangeListener(new ValueChangeListener(){

			@Override
			public void valueChange(ValueChangeEvent event) {
				
			ProductOption p=(ProductOption)event.getProperty().getValue();
			layout.addComponent(new Label(p.getOptionName()));
				
				
		}});
        
        
        
        MTextField textField = new MTextField();
        textField.selectAll();
        textField.addTextChangeListener(new TextChangeListener(){

			@Override
			public void textChange(TextChangeEvent event) {
				options.addContainerFilter("optionName",event.getText(), true, false);

			}
        	
        });

        MButton button2 = new MButton("Cancel Filter");
        
        button2.addClickListener(this::reset);
        
        layout.addComponents(myGrid,button,comboBox,textField, button2);
        layout.setMargin(true);
        layout.setSpacing(true);
        
        setContent(layout);
    }
    
    //Reset the datasource container.
    public void reset(){
    	options.removeAllContainerFilters();
    }
    
    public void removeList(){
    	//Remove the Item from the Datasource container
    	options.removeItem(myGrid.getSelectedRow());
    	//Remove the Item from the collection list
    	list.remove(myGrid.getSelectedRow());
    
    }
    
    //Create a temp List that allows the Item to be add into Grid.
    public void setTempFile(){
    	
    	for(int i=0;i<10;i++){
    		
    		ProductOption productOption = new ProductOption();
    		productOption.setOptionName("X"+i);
    		list.add(productOption);
    	}
    	
    	
    }
    

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
