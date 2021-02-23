package ca.ids.abms.config.db;

import com.google.common.base.CaseFormat;
import org.atteo.evo.inflector.English;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinTableNameSource;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;

public class CustomImplicitNamingStrategy extends SpringImplicitNamingStrategy {

    private static final long serialVersionUID = 1L;

	@Override
    public Identifier determinePrimaryTableName(ImplicitEntityNameSource source) {
        Identifier identifier = super.determinePrimaryTableName(source);

        String pluralName = NamingConverter.determinePrimaryTableName(identifier.getText());

        return new Identifier(pluralName, identifier.isQuoted());
    }

    @Override
    public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
        String owner = source.getOwningEntityNaming().getJpaEntityName();
        String nonOwner = source.getNonOwningEntityNaming().getJpaEntityName();

        String joinTableName = NamingConverter.determineJoinTableName(owner, nonOwner);

        return toIdentifier(joinTableName, source.getBuildingContext());
    }

    @Override
    public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
        String entityName = source.getEntityNaming().getJpaEntityName();
        String columnName = source.getReferencedColumnName().getText();

        String joinColumnName = NamingConverter.determineJoinColumnName(entityName, columnName);

        return toIdentifier(joinColumnName, source.getBuildingContext());
    }

    static class NamingConverter {

        // private ctor to prevent instantiation
        private NamingConverter() {
        }

        static String determinePrimaryTableName(String tableName) {
            tableName = toSnakeCase(tableName);

            return tableName.endsWith("s") ? tableName : English.plural(tableName);
        }

        static String determineJoinTableName(String owningEntityName, String nonOwningEntityName) {
            return toSnakeCase(owningEntityName) + "_" + toSnakeCase(nonOwningEntityName);
        }

        static String determineJoinColumnName(String entityName, String columnName) {
            return toSnakeCase(entityName) + "_" + columnName;
        }

        static String toSnakeCase(String name) {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        }
    }
}
