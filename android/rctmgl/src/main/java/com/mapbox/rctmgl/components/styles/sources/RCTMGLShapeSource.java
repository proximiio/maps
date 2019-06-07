package com.mapbox.rctmgl.components.styles.sources;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.rctmgl.components.mapview.RCTMGLMapView;
import com.mapbox.rctmgl.events.FeatureClickEvent;
import com.mapbox.rctmgl.utils.DownloadMapImageTask;
import com.mapbox.rctmgl.utils.ImageEntry;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by nickitaliano on 9/19/17.
 */

public class RCTMGLShapeSource extends RCTSource<GeoJsonSource> {
    private URL mURL;
    private RCTMGLShapeSourceManager mManager;

    private String mShape;

    private Boolean mCluster;
    private Integer mClusterRadius;
    private Integer mClusterMaxZoom;

    private Integer mMaxZoom;
    private Integer mBuffer;
    private Double mTolerance;
    private boolean mRemoved = false;

    private List<Map.Entry<String, ImageEntry>> mImages;
    private List<Map.Entry<String, BitmapDrawable>> mNativeImages;

    public RCTMGLShapeSource(Context context, RCTMGLShapeSourceManager manager) {
        super(context);
        mManager = manager;
    }

    @Override
    public void addToMap(final RCTMGLMapView mapView) {
        mRemoved = false;
        if (!hasNativeImages() && !hasImages()) {
            super.addToMap(mapView);
            return;
        }

        MapboxMap map = mapView.getMapboxMap();
        Style style = map.getStyle();

        if (style != null) {
            // add all images from drawables folder
            if (hasNativeImages()) {
                for (Map.Entry<String, BitmapDrawable> nativeImage : mNativeImages) {
                    style.addImage(nativeImage.getKey(),  nativeImage.getValue().getBitmap());
                }
            }
        }

        // add all external images from javascript layer
        if (hasImages()) {
            DownloadMapImageTask.OnAllImagesLoaded imagesLoadedCallback = new DownloadMapImageTask.OnAllImagesLoaded() {
                @Override
                public void onAllImagesLoaded() {
                    // don't add the ShapeSource when the it was removed while loading images
                    if (mRemoved) return;
                    RCTMGLShapeSource.super.addToMap(mapView);
                }
            };

            DownloadMapImageTask task = new DownloadMapImageTask(getContext(), map, imagesLoadedCallback);
            task.execute(mImages.toArray(new Map.Entry[mImages.size()]));
            return;
        }

        super.addToMap(mapView);
    }

    @Override
    public void removeFromMap(RCTMGLMapView mapView) {
        super.removeFromMap(mapView);
        mRemoved = true;
        if (mMap == null) return;

        Style style = this.getStyle();
        if (style != null) {
            if (hasImages()) {
                for (Map.Entry<String, ImageEntry> image : mImages) {
                    style.removeImage(image.getKey());
                }
            }

            if (hasNativeImages()) {
                for (Map.Entry<String, BitmapDrawable> image : mNativeImages) {
                    style.removeImage(image.getKey());
                }
            }
        }
    }

    @Override
    public GeoJsonSource makeSource() {
        GeoJsonOptions options = getOptions();

        if (mShape != null) {
            return new GeoJsonSource(mID, mShape, options);
        }

        return new GeoJsonSource(mID, mURL, options);
    }

    public void setURL(URL url) {
        mURL = url;

        if (mSource != null && mMapView != null && !mMapView.isDestroyed() ) {
            ((GeoJsonSource) mSource).setUrl(mURL);
        }
    }

    public void setShape(String geoJSONStr) {
        mShape = geoJSONStr;

        if (mSource != null && mMapView != null && !mMapView.isDestroyed() ) {
            ((GeoJsonSource) mSource).setGeoJson(mShape);
        }
    }

    public void setCluster(boolean cluster) {
        mCluster = cluster;
    }

    public void setClusterRadius(int clusterRadius) {
        mClusterRadius = clusterRadius;
    }

    public void setClusterMaxZoom(int clusterMaxZoom) {
        mClusterMaxZoom = clusterMaxZoom;
    }

    public void setMaxZoom(int maxZoom) {
        mMaxZoom = maxZoom;
    }

    public void setBuffer(int buffer) {
        mBuffer = buffer;
    }

    public void setTolerance(double tolerance) {
        mTolerance = tolerance;
    }

    public void setImages(List<Map.Entry<String, ImageEntry>> images) {
        mImages = images;
    }

    public void setNativeImages(List<Map.Entry<String, BitmapDrawable>> nativeImages) {
        mNativeImages = nativeImages;
    }

    public void onPress(Feature feature) {
        mManager.handleEvent(FeatureClickEvent.makeShapeSourceEvent(this, feature));
    }

    private GeoJsonOptions getOptions() {
        GeoJsonOptions options = new GeoJsonOptions();

        if (mCluster != null) {
            options.withCluster(mCluster);
        }

        if (mClusterRadius != null) {
            options.withClusterRadius(mClusterRadius);
        }

        if (mClusterMaxZoom != null) {
            options.withClusterMaxZoom(mClusterMaxZoom);
        }

        if (mMaxZoom != null) {
            options.withMaxZoom(mMaxZoom);
        }

        if (mBuffer != null) {
            options.withBuffer(mBuffer);
        }

        if (mTolerance != null) {
            options.withTolerance(mTolerance.floatValue());
        }

        return options;
    }

    private boolean hasImages() {
        return mImages != null && mImages.size() > 0;
    }

    private boolean hasNativeImages() {
        return mNativeImages != null && mNativeImages.size() > 0;
    }
}
