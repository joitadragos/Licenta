package ro.licenta.views.notice;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import ro.licenta.backend.dao.CurrentUser;
import ro.licenta.backend.data.Notice;
import ro.licenta.backend.service.NoticeService;
import ro.licenta.backend.user.User;
import ro.licenta.views.main.MainView;


@Route(value = "notice-send", layout = MainView.class)
@RouteAlias(value = "notice-send", layout = MainView.class)
@PageTitle("Send Notice")
public class NoticeSend extends Div implements AfterNavigationObserver {
    @Autowired
    private NoticeService noticeService;
    private Grid<Notice> noticeGrid;

    private TextField introduceName = new TextField();
    private TextArea introduceNote= new TextArea();
    private Button sendButton = new Button("Send");
    private Button deleteButton = new Button("Delete");
    private Button cancelButton = new Button("Cancel");


    private Binder<Notice> binder;


    public NoticeSend() {
        //Configure Grid

        noticeGrid = new Grid<>();
        noticeGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        noticeGrid.addColumn(Notice::getName).setHeader("Name");
        noticeGrid.addColumn(Notice::getText).setHeader("Note");

        binder = new Binder<>(Notice.class);

        noticeGrid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        cancelButton.addClickListener(e -> noticeGrid.asSingleSelect().clear());


        sendButton.addClickListener(e ->{
            Notice notice = binder.getBean();
            if(noticeService.store(notice)>0){
                noticeGrid.setItems(noticeService.findNotes());
            }else{
                Notification.show("Save Error");
            }
        });
        deleteButton.addClickListener(e ->{
            Notice notice = binder.getBean();
            if(noticeService.deleteNotice(notice)>0){
                noticeGrid.setItems(noticeService.findNotes());
            }else{
                Notification.show("Delete Error");
            }
        });


        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        editorDiv.setTitle("Change User Details");
        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, introduceName, "Introduce Name");
        addFormItem(editorDiv, formLayout, introduceNote, "Introduce Note");

        createButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);
    }

    private void createButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        buttonLayout.add(deleteButton, sendButton,cancelButton);
        editorDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();

        splitLayout.addToPrimary(wrapper);
        wrapper.add(noticeGrid);
    }
    private void addFormItem(Div wrapper, FormLayout formLayout,
                             AbstractField field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        noticeGrid.setItems(noticeService.findNotes());
    }
    private void populateForm(Notice value) {
        // Value can be null as well, that clears the form

        //binder.readBean(value); // commented out
        if ( value == null ) {
            value = new Notice();
        }
        binder.setBean(value);
    }

}
