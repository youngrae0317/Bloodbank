package database;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecordeDB {

    public static Object[][] fetchBloodRecords() {
        // JOIN을 사용하여 헌혈자와 직원의 이름과 아이디를 조합
        String query = "SELECT " +
                "    헌혈기록번호, " +
                "    헌혈자.이름 || '(' || 헌혈자.회원_ID || ')' AS 회원정보, " +
                "    직원.이름 || '(' || 직원.직원_ID || ')' AS 직원정보, " +
                "    헌혈종류, 헌혈량, 헌혈일자 " +
                "FROM BLOODBANK.헌혈기록 " +
                "JOIN BLOODBANK.헌혈자 ON 헌혈기록.회원_ID = 헌혈자.회원_ID " +
                "JOIN BLOODBANK.직원 ON 헌혈기록.담당직원_ID = 직원.직원_ID";

        try (Connection connection = DBConnect.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // 데이터를 동적으로 저장할 리스트
            java.util.List<Object[]> dataList = new java.util.ArrayList<>();

            // 결과를 순차적으로 읽어 리스트에 추가
            while (resultSet.next()) {
                Object[] row = new Object[6];
                row[0] = resultSet.getInt("헌혈기록번호");
                row[1] = resultSet.getString("회원정보"); // 회원정보: 이름(아이디)
                row[2] = resultSet.getString("직원정보"); // 직원정보: 이름(아이디)
                row[3] = resultSet.getString("헌혈종류");
                row[4] = resultSet.getInt("헌혈량") + "ml";
                row[5] = resultSet.getDate("헌혈일자").toString();
                dataList.add(row);
            }

            // 리스트를 배열로 변환
            return dataList.toArray(new Object[0][0]);

        } catch (SQLException e) {
            e.printStackTrace();
            return new Object[0][0]; // 오류 발생 시 빈 배열 반환
        }
    }



    public static void loadBloodRecords(DefaultTableModel tableModel) {
        Object[][] data = fetchBloodRecords();
        tableModel.setRowCount(0); // 기존 데이터 삭제
        for (Object[] row : data) {
            tableModel.addRow(row); // 새 데이터 추가
        }
    }
}