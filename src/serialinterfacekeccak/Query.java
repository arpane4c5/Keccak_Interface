package serialinterfacekeccak;

import java.sql.ResultSet;
import java.util.Vector;

/**
 *
 * @author Arpan Gupta
 */
public class Query {

    public Query() {
    }

    public final Vector<String[]> getServerList() {
        Vector<String[]> servers = null;
        DBConnector qp = new DBConnector();
        try {
            qp.init();
            try {
                qp.connect();
                try {
                    String query = "select Ip, ShareSize , Status, ServerType from servers;";
                    ResultSet rs = qp.getRecords(query);
                    if (rs != null) {
                        servers = new Vector<String[]>();
                        while (rs.next()) {
                            String ip = rs.getString(1);
                            long s = rs.getLong(2);
                            String size = getFileSize(s);
                            String status = "Inactive";
                            if (rs.getBoolean(3) == true) {
                                status = "Active";
                            }
                            String type = "FTP";
                            int temp = rs.getInt(4);
                            if (temp == 1) {
                                type = "SMB";
                            }
                            String row[] = {ip, size, status, type};
                            servers.add(row);
                        }
                    }
                } catch (Exception g) {
                    System.err.print("Query.java :Could not query the database.\n" + g);
                    servers = null;
                } finally {
                    qp.close();
                    return servers;
                }
            } catch (Exception f) {
                System.err.print("Query.java :Could not connect to database.\n" + f);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Query.java :Could not read from DatabaseConnectionParameters.properties.\n" + e);
            return null;
        }
    }

    public final String getFileSize(final long sizeInBytes) {

        double x = (double) ((int) ((sizeInBytes + 0.005) * 100)) / 100.0;
        String size = Double.toString(x) + " B";
        if ((sizeInBytes / 1099511627776L) > 0) {
            double temp = (double) (int) ((sizeInBytes / 1099511627776.0 + 0.005) * 100) / 100.0;
            size = Double.toString(temp) + " TB";
        } else if ((sizeInBytes / 1073741824) > 0) {
            double temp = (double) (int) ((sizeInBytes / 1073741824.0 + 0.005) * 100) / 100.0;
            size = Double.toString(temp) + " GB";
        } else if ((sizeInBytes / 1048576) > 0) {
            double temp = (double) (int) ((sizeInBytes / 1048576.0 + 0.005) * 100) / 100.0;
            size = Double.toString(temp) + " MB";
        } else if ((sizeInBytes / 1024) > 0) {
            double temp = (double) (int) ((sizeInBytes / 1024.0 + 0.005) * 100) / 100.0;
            size = Double.toString(temp) + " KB";
        }
        return size;

    }

    public static void main(String args[]) {
        Query q = new Query();
        Vector<String[]> result = q.getServerList();
        if (result == null) {
            System.out.println("Query.main :result = null");
        } else {
            System.out.println("Query.main :result.size() = " + result.size());
        }
    }
}
