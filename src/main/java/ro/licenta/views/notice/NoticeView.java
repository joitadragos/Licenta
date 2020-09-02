package ro.licenta.views.notice;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ro.licenta.backend.data.Notice;
import ro.licenta.backend.service.NoticeService;
import ro.licenta.views.main.MainView;


@Route(value = "notice-view", layout = MainView.class)
@PageTitle("NoticeView")
public class NoticeView extends Div implements AfterNavigationObserver {
    public static final String VIEW_NAME = "NoticeView";
    @Autowired
    private NoticeService noticeService;
    private Grid<Notice> noticeGrid;

    private TextField introduceName = new TextField();
    private TextArea introduceNote= new TextArea();


    private BeanValidationBinder<Notice> binder;


    public NoticeView() {
        //Configure Grid

        noticeGrid = new Grid<>();
        noticeGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        noticeGrid.addColumn(Notice::getName).setHeader("Name");
        noticeGrid.addColumn(Notice::getText).setHeader("Note");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);

        add(splitLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();

        splitLayout.addToPrimary(wrapper);
        wrapper.add(noticeGrid);
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        noticeGrid.setItems(noticeService.findNotes());
    }

}
