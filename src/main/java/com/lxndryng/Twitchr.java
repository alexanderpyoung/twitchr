package com.lxndryng;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import java.util.List;
import java.util.Map;

@Theme("mytheme")
@SuppressWarnings("serial")
public class Twitchr extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = Twitchr.class, widgetset = "com.lxndryng.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        setPollInterval(50);

        ThemeResource resource = new ThemeResource("liquidcat.gif");

        final Image image = new Image("", resource);

        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        final TextField channel = new TextField();
        final Button button = new Button("Click Me");
        layout.addComponent(channel);
        layout.addComponent(button);

        class WorkThread extends Thread {
            public void run() {
                try {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e) {}
                try {
                    TwitchUrlProcess twitchurl = new TwitchUrlProcess(channel.getValue());
                    List<String> twitchInfo = twitchurl.getInfo();
                    String twitchPlaylist = twitchurl.getPlaylist(twitchInfo.get(0), twitchInfo.get(1));
                    List<Map<String, String>> HLSInfo = twitchurl.getHLS(twitchPlaylist);
                    Label url = new Label(HLSInfo.get(0).get("url"));
                    url.addAttachListener(new Label.AttachListener() {
                        public void attach(AttachEvent attach) {
                            layout.removeComponent(image);
                        }
                    });
                    layout.addComponent(url);
                }
                catch (Exception e) {
                    layout.removeComponent(image);
                    layout.addComponent(new Label("An Error occurred"));
                }
            }
        }

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                layout.addComponent(image);
                final WorkThread thread = new WorkThread();
                thread.start();
            }
        });
    }

}
