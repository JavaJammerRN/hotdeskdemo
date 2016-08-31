$(function() {
      var format = 'image/png';
      var bounds = [-70, 0, 50, 50];
      var untiled = new ol.layer.Image({
        source: new ol.source.ImageWMS({
          ratio: 1,
          url: 'http://ukl5cg6195g69:8080/geoserver/Hot-Desk-DBlinked/wms',
          params: {'FORMAT': format,
                   'VERSION': '1.1.1',
                STYLES: '',
                LAYERS: 'Hot-Desk-DBlinked:DB-OBH-mod',
          }
        })
      });
      var tiled = new ol.layer.Tile({
        visible: false,
        source: new ol.source.TileWMS({
          url: 'http://ukl5cg6195g69:8080/geoserver/Hot-Desk-DBlinked/wms',
          params: {'FORMAT': format,
                   'VERSION': '1.1.1',
                   tiled: true,
                STYLES: '',
                LAYERS: 'Hot-Desk-DBlinked:DB-OBH-mod',
          }
        })
      });
      var projection = new ol.proj.Projection({
          code: 'EPSG:4326',
          units: 'degrees',
          axisOrientation: 'neu'
      });
      var map = new ol.Map({
        controls: ol.control.defaults({
          attribution: false
        })
        ,
        target: 'map',
        layers: [
          untiled,
          tiled
        ],
        view: new ol.View({
           projection: projection
        })
      });

      map.getView().fit(bounds, map.getSize());
    });
