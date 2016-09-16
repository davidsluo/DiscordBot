package commands;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 9/15/2016.
 */
public class MonospaceTable {
    private List<String[]> table = new ArrayList<>();

    private int[] colWidths;

    public MonospaceTable(String... columns) {
        table.add(columns);
        colWidths = new int[columns.length];
        for (int i = 0; i < colWidths.length; i++) {
            colWidths[i] = columns[i].length();
        }
    }

    public void add(String... row) {
        if (row.length != table.get(0).length)
            throw new IndexOutOfBoundsException("Number of row columns not equal to table columns!");
        else {
            table.add(row);

            for (int i = 0; i < colWidths.length; i++)
                if (row[i].length() > colWidths[i])
                    colWidths[i] = row[i].length();
        }
    }

    public String[] get(int rowIndex) {
        return table.get(rowIndex);
    }

    @Override
    public String toString() {

        StringBuilder output         = new StringBuilder();
        int           tableCharWidth = 0;
        String        rowPattern     = "";
        String        separator      = "";

        for (int width : colWidths) {
            tableCharWidth += width;
            tableCharWidth++;

            rowPattern += "|%-" + width + "s";
        }

        tableCharWidth++;
        rowPattern += "|\n";

        for (int i = 0; i < tableCharWidth; i++) {
            separator+="-";
        }

        separator+="\n";

        for (String[] row : table) {
            output.append(String.format(rowPattern, row));

            if (table.indexOf(row) == 0)
                output.append(separator);

        }

        return output.toString();
    }
}
