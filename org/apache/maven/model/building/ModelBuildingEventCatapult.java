package org.apache.maven.model.building;

interface ModelBuildingEventCatapult {
  public static final ModelBuildingEventCatapult BUILD_EXTENSIONS_ASSEMBLED = new ModelBuildingEventCatapult() {
      public void fire(ModelBuildingListener listener, ModelBuildingEvent event) {
        listener.buildExtensionsAssembled(event);
      }
    };
  
  void fire(ModelBuildingListener paramModelBuildingListener, ModelBuildingEvent paramModelBuildingEvent);
}
