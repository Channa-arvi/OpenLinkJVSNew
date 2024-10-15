package com.garinternal.common.util;

/*
File Name:                      GarAnnotationScanner.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Script to scan for enums that declare a particular annotation

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class GarAnnotationScannerUtil {

    /**
     * Constructor
     */
    private GarAnnotationScannerUtil() {
        // do nothing
    }

    /**
     * Get list of classes having an annotation specified in them
     * 
     * @param packageName     Name of the package prefixed with Project Name and separated with "."
     * @param annotationClass Name of the annotation class to search for
     * @param logger          GarLogger instance
     * @return Map containing Classes and their list of annotations
     * @throws OException {@link OException}
     */
    public static Map<Class<?>, Annotation> getClassListHavingAnnotation(String packageName, Class<?> annotationClass, GarLogger logger)
        throws OException {
        Map<Class<?>, Annotation> classAnnotationMap = new HashMap<>();
        Table                     tblClassList       = Util.NULL_TABLE;

        try {

            // Load the classLoader which loads this class.
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            // Change the package structure to directory structure
            String packagePath = packageName.replace('.', '/');
            URL    urls        = classLoader.getResource(packagePath);

            // if the classLoader is able to fetch the classes,
            if (urls != null) {

                // Get all the class files in the specified URL Path.
                File   folder  = new File(urls.getPath());
                File[] classes = folder.listFiles();

                int size = classes.length;

                for (int indexId = 0; indexId < size; indexId++) {
                    String classPath = classes[indexId].getName();

                    if (GarHasAValue.hasAValue(classPath, true) && !classPath.endsWith(".class")) {
                        break;
                    }

                    int    index     = classPath.indexOf(".");
                    String className = classes[indexId].getName().substring(0, index);
                    getClassAnnotationMap(packageName, className, annotationClass, classAnnotationMap);
                }

            } else {
                // if the classLoader is not able to fetch the classes, get them from DB
                tblClassList = getClassListFromDB(packagePath, logger);

                if (!GarHasAValue.isTableEmpty(tblClassList)) {
                    int numRows = tblClassList.getNumRows();

                    for (int rowNum = 1; rowNum <= numRows; rowNum++) {
                        String className = tblClassList.getString("node_name", rowNum);
                        getClassAnnotationMap(packageName, className, annotationClass, classAnnotationMap);
                    }

                }

            }

        } catch (ClassNotFoundException e) {
            throw new OException(e);
        } finally {
            GarSafe.destroy(tblClassList);
        }

        return classAnnotationMap;
    }

    /**
     * Get class annotation map
     * 
     * @param packageName        Package Name
     * @param className          Class Name
     * @param annotationClass    Annotation Class
     * @param classAnnotationMap Map to store list of annotations for each class
     * @throws ClassNotFoundException
     */
    private static void getClassAnnotationMap(String packageName, String className, Class<?> annotationClass,
        Map<Class<?>, Annotation> classAnnotationMap) throws ClassNotFoundException {
        String       classNamePath = packageName.substring(packageName.indexOf('.') + 1, packageName.length()) + "." + className;
        Class<?>     repoClass     = Class.forName(classNamePath);
        Annotation[] annotations   = repoClass.getAnnotations();

        for (int annotationIndex = 0; annotationIndex < annotations.length; annotationIndex++) {

            if (annotationClass == annotations[annotationIndex].annotationType()) {
                classAnnotationMap.put(repoClass, annotations[annotationIndex]);
                break;
            }

        }

    }

    /**
     * Get list of classes in a package from DB
     * 
     * @param packagePath Package path prefixed with Project Name and separated by /
     * @param logger      GarLogger instance
     * @return
     * @throws OException
     */
    private static Table getClassListFromDB(String packagePath, GarLogger logger) throws OException {
        StringBuilder sql = new StringBuilder();

        sql.append("DECLARE @flag INT ") //
            .append("\n DECLARE @nodeId INT") //
            .append("\n DECLARE @index INT") //
            .append("\n DECLARE @path varchar(2500)") //
            .append("\n DECLARE @nodeName varchar(255)") //
            .append("\n DECLARE @separator varchar(5)") //
            .append("\n DECLARE @found INT") //
            .append("\n ") //
            .append("\n SET @found=1")//
            .append("\n SET @flag=1") //
            .append("\n SET @nodeId=12") //
            .append("\n SET @path='" + packagePath + "'") //
            .append("\n SET @separator='/'") //
            .append("\n ")//
            .append("\n WHILE (@flag = 1)") //
            .append("\n BEGIN") //
            .append("\n     SET @index=CHARINDEX(@separator, @path)") //
            .append("\n ") //
            .append("\n     IF @index > 0") //
            .append("\n         BEGIN") //
            .append("\n             SET @nodeName=SUBSTRING(@path,0,@index)") //
            .append("\n             SET @nodeId=(SELECT node_id") //
            .append("\n                          FROM   dir_node") //
            .append("\n                          WHERE  parent_node_id=@nodeId") //
            .append("\n                            AND  node_name = @nodeName)") //
            .append("\n ") //
            .append("\n             IF @nodeId <= 0") //
            .append("\n                 BEGIN") //
            .append("\n                     SET @found=0") //
            .append("\n                         BREAK") //
            .append("\n                 END") //
            .append("\n             SET @path=SUBSTRING(@path,@index+1, LEN(@path))") //
            .append("\n         END") //
            .append("\n     ELSE IF LEN(RTRIM(LTRIM(@path))) > 0") //
            .append("\n         BEGIN") //
            .append("\n             SET @nodeId=(SELECT node_id") //
            .append("\n                          FROM   dir_node") //
            .append("\n                          WHERE  parent_node_id=@nodeId") //
            .append("\n                            AND  node_name = @path)") //
            .append("\n             SET @path=''") //
            .append("\n ") //
            .append("\n             IF @nodeId <= 0") //
            .append("\n                 BEGIN") //
            .append("\n                     SET @found=0") //
            .append("\n                     BREAK") //
            .append("\n                 END") //
            .append("\n         END ") //
            .append("\n     ELSE") //
            .append("\n         BEGIN") //
            .append("\n             BREAK") //
            .append("\n         END") //
            .append("\n END") //
            .append("\n ") //
            .append("\n SET @nodeId = (CASE WHEN @found=1 THEN @nodeId ELSE 0 END)") //
            .append("\n ") //
            .append("\n SELECT DISTINCT node_name ") //
            .append("\n FROM   dir_node") //
            .append("\n WHERE  parent_node_id = @nodeId") //
            .append("\n   AND  node_type = 7");

        return GarStrict.query(sql.toString(), logger);
    }
}
