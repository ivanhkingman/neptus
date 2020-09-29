package pt.lsts.neptus.plugins.ho4;

import java.awt.Color;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import com.google.common.eventbus.Subscribe;

import pt.lsts.imc.IMCAddressResolver;
import pt.lsts.imc.PathControlState;
import pt.lsts.imc.PlanDB;
import pt.lsts.imc.Rpm;
import pt.lsts.imc.Temperature;
import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.console.ConsoleLayer;
import pt.lsts.neptus.console.events.ConsoleEventMainSystemChange;
import pt.lsts.neptus.plugins.PluginDescription;
import pt.lsts.neptus.plugins.PluginDescription.CATEGORY;
import pt.lsts.neptus.renderer2d.StateRenderer2D;
import pt.lsts.neptus.types.coord.LocationType;
import pt.lsts.neptus.types.vehicle.VehicleType;
import pt.lsts.neptus.types.vehicle.VehiclesHolder;


@PluginDescription(author="Manuel R.", category=CATEGORY.UNSORTED, name="Myplugin", version = "0.8", description = "This is my first plugin")
public class MyFirstPlugin extends ConsoleLayer {

    private VehicleType vehicle;
    private IMCAddressResolver res = new IMCAddressResolver();
    private int mainvehicleId;
    private short rpmVal = -1;
    private double tempVal = Double.MAX_VALUE;
    private LocationType loc = null;
    private String planId = ""; // addition is part of my home work

    public MyFirstPlugin() {
        NeptusLog.pub().info("This is my first plugin");
    }
    
    @Override
    public void paint(Graphics2D g, StateRenderer2D renderer) {
        
        super.paint(g, renderer);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.BLACK);
        
        // Code for drawing selected vehicle
        StringBuilder str0 = new StringBuilder();
        str0.append("Selected Vehicle: ");
        str0.append(getConsole().getMainSystem());
        g2.drawString(str0.toString(), 15, 15);
 
        // Code for drawing RPM value
        StringBuilder str1 = new StringBuilder();
        str1.append("RPM: ");
        if (rpmVal != -1)
            str1.append(rpmVal);
        else
            str1.append("N/A");
        g2.drawString(str1.toString(), 15, 30);
        
        // Code for drawing temperature
        DecimalFormat df2 = new DecimalFormat("#.##");
        StringBuilder str2 = new StringBuilder();
        str2.append("Temp: ");
        if (tempVal != Double.MAX_VALUE)
            str2.append(df2.format(tempVal));
        else
            str2.append("N/A");
        g2.drawString(str2.toString(), 15, 45);
        
        // Code for drawing target location (home-work)
        g2.drawString("Target Coord.: "+ Double.toString(loc.getLatitudeDegs()) + ", " +Double.toString(loc.getLongitudeDegs()), 15, 60);
        
        // Code for drawing selected vehicles plan name
        StringBuilder str3 = new StringBuilder();
        str3.append("Plans: ");
        if (planId != "")
            str3.append(planId);
        else
            str3.append("N/A");
        g2.drawString(str3.toString(), 15, 75);
        
        g2.dispose();
    }
    
    @Subscribe
    public void mainVehicleChangeNotification(ConsoleEventMainSystemChange evt) {
        rpmVal = -1;
        tempVal = Double.MAX_VALUE;
        System.out.println("Selected vehicle is: " + evt.getCurrent());
        vehicle = VehiclesHolder.getVehicleById(evt.getCurrent());
        
        mainvehicleId = res.resolve(vehicle.getId());
        System.out.println(vehicle.toString() + " "+ mainvehicleId);
    }
    
    @Subscribe
    public void consume(Rpm rpm) {
        if (rpm.getSrc() == mainvehicleId) {
            rpmVal = rpm.getValue();
        }
    }
    
    
    //HOME-WORK
    @Subscribe
    public void consume(PathControlState pcs) {
        if (pcs.getSrc() == mainvehicleId) {
            loc = new LocationType(Math.toDegrees(pcs.getEndLat()), Math.toDegrees(pcs.getEndLon()));
        }
    }

    @Subscribe
    public void consume(Temperature temp) {
        if (temp.getSrc() == mainvehicleId) {
            tempVal = temp.getValue();
        }
    }
    
    // My Home-Work below this comment //
    
    @Subscribe
    public void consume(PlanDB db) {
        if (db.getSrc() == mainvehicleId) {
            planId = db.getArg().getMessageList("plans_info").get(8).toString();
        }
    }
    
    // My Homework above this comment //

    @Override
    public boolean userControlsOpacity() {
        return false;
    }

    @Override
    public void initLayer() {
        
    }

    @Override
    public void cleanLayer() {
        
    }

}
