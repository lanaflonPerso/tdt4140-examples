package tdt4140.gr1800.app.ui;

import java.util.Iterator;

import fxmapcontrol.Location;
import fxmapcontrol.MapBase;
import fxmapcontrol.MapItemsControl;
import fxmapcontrol.MapNode;
import fxmapcontrol.MapTileLayer;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import tdt4140.gr1800.app.core.App;
import tdt4140.gr1800.app.core.GeoLocated;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.IGeoLocationsListener;
import tdt4140.gr1800.app.core.LatLong;

public class FxAppController implements IGeoLocationsListener {

	@FXML
	private FileMenuController fileMenuController;

	@FXML
	private ComboBox<String> geoLocationsSelector;

	@FXML
	private MapBase mapView;
	
	private MapItemsControl<MapNode> markersParent;
	
	@FXML
	private Slider zoomSlider;
	
	private App app;

	@FXML
	public void initialize() {
		app = new App();
		fileMenuController.setDocumentStorage(app.getDocumentStorage());
		fileMenuController.setOnDocumentChanged(documentStorage -> initMapMarkers());

		geoLocationsSelector.getSelectionModel().selectedItemProperty().addListener((stringProperty, oldValue, newValue) -> updateGeoLocations());

		mapView.getChildren().add(MapTileLayer.getOpenStreetMapLayer());
		zoomSlider.valueProperty().addListener((prop, oldValue, newValue) -> {
			mapView.setZoomLevel(zoomSlider.getValue());			
		});
		markersParent = new MapItemsControl<MapNode>();
		mapView.getChildren().add(markersParent);
		
		app.addGeoLocationsListener(this);
	}

	private Object updateGeoLocations() {
		return null;
	}

	private void initMapMarkers() {
		markersParent.getItems().clear();
		geoLocationsSelector.getItems().clear();
		for (String geoLocationName : app.getGeoLocationNames()) {
			GeoLocations geoLocations = app.getGeoLocations(geoLocationName);
			MapMarker lastMarker = null;
			for (GeoLocated geoLoc : geoLocations) {
				MapMarker mapMarker = new MapMarker(geoLoc.getLatLong());
				markersParent.getItems().add(mapMarker);
				if (geoLocations.isPath() && lastMarker != null) {
					MapPathLine pathLine = new MapPathLine(lastMarker, mapMarker);
					markersParent.getItems().add(pathLine);
				}
				lastMarker = mapMarker;
			}
			geoLocationsSelector.getItems().add(geoLocationName + " (" + geoLocations.size() + ")");
		}
		LatLong center = getCenter(null);
		mapView.setCenter(new Location(center.latitude, center.longitude));
	}

	private LatLong getCenter(GeoLocations geoLocations) {
		double latSum = 0.0, lonSum = 0.0;
		int num = 0;
		Iterator<String> names = null;
		if (geoLocations == null) {
			names = app.getGeoLocationNames().iterator();
		}
		while (geoLocations != null || (names != null && names.hasNext())) {
			if (names != null) {
				geoLocations = app.getGeoLocations(names.next());
			}
			for (GeoLocated geoLoc : geoLocations) {
				double lat = geoLoc.getLatitude(), lon = geoLoc.getLongitude();
				latSum += lat;
				lonSum += lon;
				num++;
			}
			if (names != null) {
				geoLocations = null;
			}
		}
		return new LatLong(latSum / num, lonSum / num);
	}

	@Override
	public void geoLocationsUpdated(GeoLocations geoLocations) {
		initMapMarkers();
	}
}
