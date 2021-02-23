package ca.ids.abms.modules.common.mappers;

import org.mapstruct.Mapper;

@Mapper
public abstract class ClassMapper {

    public Class stringToClass(String string) throws ClassNotFoundException {
        if (string == null)
            return null;
        return Class.forName(string);
    }

    public String classToString(Class clazz) {
        if (clazz == null)
            return null;
        return clazz.getName();
    }
}
