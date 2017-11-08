package frogma;

import frogma.gameobjects.models.BasicGameObject;

public class ObjectConstructor {
    private String className;
    private int subType;

    public ObjectConstructor(String className, int subType) {
        this.className = className;
        this.subType = subType;
    }

    public BasicGameObject createInstance(GameEngine referrer, int objIndex, int[] param) {
        Class objClass;
        BasicGameObject dObj;
        Integer theSubType = new Integer(this.subType);
        Integer theObjIndex = new Integer(objIndex);
        Integer[] theParams = new Integer[10];

        for (int i = 0; i < 10; i++) {
            theParams[i] = new Integer(param[i]);
        }

        try {
            objClass = Class.forName(className);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("FATAL ERROR: Class " + className + " is missing. Unable to create object instance.");
            return null;
        }

        try {
            dObj = (BasicGameObject) objClass.getConstructor(new Class[]{GameEngine.class, Integer.class, Integer.class, Integer[].class}).newInstance(new Object[]{referrer, theSubType, theObjIndex, theParams});
        } catch (Exception e) {
            System.out.println("The class " + this.className + " does not follow object conventions, and cannot be instantiated.");
            return null;
        }

        return dObj;
    }


}