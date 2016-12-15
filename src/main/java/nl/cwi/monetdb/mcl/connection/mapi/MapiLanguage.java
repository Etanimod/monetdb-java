package nl.cwi.monetdb.mcl.connection.mapi;

import nl.cwi.monetdb.mcl.connection.IMonetDBLanguage;

/**
 * Created by ferreira on 11/30/16.
 */
public enum MapiLanguage implements IMonetDBLanguage {

    /** the SQL language */
    LANG_SQL(new String[]{"s", "\n;", "\n;\n"}, new String[]{"X", null, "\nX"}, "sql"),
    /** the MAL language (officially *NOT* supported) */
    LANG_MAL(new String[]{null, ";\n", ";\n"}, new String[]{null, null, null}, "mal"),
    /** an unknown language */
    LANG_UNKNOWN(null, null, "unknown");

    MapiLanguage(String[] queryTemplates, String[] commandTemplates, String representation) {
        this.queryTemplates = queryTemplates;
        this.commandTemplates = commandTemplates;
        this.representation = representation;
    }

    private final String[] queryTemplates;

    private final String[] commandTemplates;

    private final String representation;

    @Override
    public String getQueryTemplateIndex(int index) {
        return queryTemplates[index];
    }

    @Override
    public String getCommandTemplateIndex(int index) {
        return commandTemplates[index];
    }

    @Override
    public String[] getQueryTemplates() {
        return queryTemplates;
    }

    @Override
    public String[] getCommandTemplates() {
        return commandTemplates;
    }

    @Override
    public String getRepresentation() {
        return representation;
    }

    public static MapiLanguage GetLanguageFromString(String language) {
        switch (language) {
            case "sql":
                return LANG_SQL;
            case "mal":
                return LANG_MAL;
            default:
                return LANG_UNKNOWN;
        }
    }
}