package ro.licenta.views;

import java.util.Arrays;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import ro.licenta.backend.dao.AccessControlFactory;
import ro.licenta.backend.dao.UserAuthenticationDAO;
import ro.licenta.views.admin.MasterDetailView;
import ro.licenta.views.notice.NoticeSend;
import ro.licenta.views.notice.NoticeView;
import ro.licenta.views.inventory.InventoryView;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@PWA(name = "beehouseapp", shortName = "beehouseapp",  enableInstallPrompt = false)
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@CssImport("./styles/views/main/main-view.css")
@Route("MainView")
public class MainView extends AppLayout {

    private final Tabs menu;
    private H1 viewTitle;
    private Button logoutButton;

    public MainView() {
        setPrimarySection(Section.DRAWER);

        addToNavbar(true, createHeaderContent());
        menu = createMenu();
        addToDrawer(createDrawerContent(menu));
    }

    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        viewTitle = new H1();
        layout.add(viewTitle);
        layout.add(new Image("images/user.svg", "Avatar"));
        return layout;
    }

    private Component createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new Image("images/logo.png", "beehouseapp logo"));
        logoLayout.add(new H1("beehouseapp"));
        logoutButton = createMenuButton("Logout", VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(e -> logout());
        logoutButton.getElement().setAttribute("title", "Logout (Ctrl+L)");
        logoutButton.addClickShortcut(Key.KEY_L, KeyModifier.CONTROL);

        layout.add(logoLayout, menu,logoutButton);
        return layout;
    }
    private void registerAdminIfApplicable(UserAuthenticationDAO authenticationDAO){
        if(authenticationDAO.isUserInRole(UserAuthenticationDAO.ADMIN_ROLE_NAME)
        && !RouteConfiguration.forSessionScope()
        .isRouteRegistered(MasterDetailView.class)){
            RouteConfiguration.forSessionScope().setRoute(MasterDetailView.VIEW_NAME, MasterDetailView.class, MainView.class);
        }
    }
    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");

        final UserAuthenticationDAO userAuthenticationDAO = AccessControlFactory.getInstance().createAccessControl();
        if(userAuthenticationDAO.isUserInRole(UserAuthenticationDAO.ADMIN_ROLE_NAME)){
            tabs.add(createMenuItemsforAdmin());
        }else{
            tabs.add(createMenuItems());

        }
        return tabs;
    }
    private Button createMenuButton(String caption, Icon icon) {
        final Button routerButton = new Button(caption);
        routerButton.setClassName("menu-button");
        routerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        routerButton.setIcon(icon);
        icon.setSize("24px");
        return routerButton;
    }
    private void logout() {
        AccessControlFactory.getInstance().createAccessControl().signOut();
    }

    private Component[] createMenuItems() {
        RouterLink[] links = new RouterLink[] {
                new RouterLink("NoticeView", NoticeView.class),
                new RouterLink("User Details ", UserDetailView.class),
                new RouterLink("Inventory", InventoryView.class)

        };
        return Arrays.stream(links).map(MainView::createTab).toArray(Tab[]::new);
    }
    private Component[] createMenuItemsforAdmin(){
        RouterLink[] links = new RouterLink[]{
                new RouterLink("Send Note", NoticeSend.class),
                new RouterLink("Note", NoticeView.class),
                new RouterLink("Admin Details ", MasterDetailView.class),
        };
        return Arrays.stream(links).map(MainView::createTab).toArray(Tab[]::new);
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.add(content);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        updateChrome();
    }

    private void updateChrome() {
        getTabWithCurrentRoute().ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabWithCurrentRoute() {
        String currentRoute = RouteConfiguration.forSessionScope()
                .getUrl(getContent().getClass());
        return menu.getChildren().filter(tab -> hasLink(tab, currentRoute))
                .findFirst().map(Tab.class::cast);
    }

    private boolean hasLink(Component tab, String currentRoute) {
        return tab.getChildren().filter(RouterLink.class::isInstance)
                .map(RouterLink.class::cast).map(RouterLink::getHref)
                .anyMatch(currentRoute::equals);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }
}
