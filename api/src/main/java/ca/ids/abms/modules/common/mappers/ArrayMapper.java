package ca.ids.abms.modules.common.mappers;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = { ClassMapper.class })
public abstract class ArrayMapper {

    public abstract Class[] classListToClassArray(List<Class> list);
    public abstract List<Class> classArrayToClassList(Class[] array);

    public abstract Object[] objectListToObjectArray(List<Object> list);
    public abstract List<Object> objectArrayToObjectList(Object[] array);

    public abstract String[] stringListToStringArray(List<String> list);
    public abstract List<String> stringArrayToStringList(String[] array);

    public abstract Class[] stringListToClassArray(List<String> list) throws ClassNotFoundException;
    public abstract List<Class> stringArrayToClassList(String[] array) throws ClassNotFoundException;

    public abstract String[] classListToStringArray(List<Class> list);
    public abstract List<String> classArrayToStringList(Class[] array);
}
