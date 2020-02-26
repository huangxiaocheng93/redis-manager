package com.vincent.h.controller;

import com.vincent.h.model.HashMapData;
import com.vincent.h.model.RedisConfigList;
import com.vincent.h.service.ConfigService;
import com.vincent.h.service.RedisClusterService;
import com.vincent.h.service.ServiceFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description  主操作界面controller
 *
 * @author huangxiaocheng
 * @date 2020/2/23 15:07
 */
public class MainController {

    @FXML
    private TextField cacheKey;

    @FXML
    private TextField cacheTTL;

    @FXML
    private ListView<String> envList;

    @FXML
    private ListView<String> cacheKeyList;

    @FXML
    private Label totalKey;

    @FXML
    private TextField searchKeyword;

    @FXML
    private TextArea valueTextArea;

    @FXML
    private TableView valueTableView;

    private RedisClusterService redisClusterService;

    private static ObservableList<String> keyList = FXCollections.observableArrayList();

    /**
     * 初始化方法
     */
    @FXML
    private void initialize() {
        ConfigService configService = ServiceFactory.getService(ConfigService.class);
        RedisConfigList redisConfigList = configService.queryAllConfig();

        // 读取配置的环境信息
        ObservableList<String> values = FXCollections.observableArrayList();
        if(redisConfigList != null && redisConfigList.getConfigList().size() > 0) {
            redisConfigList.getConfigList().forEach(x -> {
                values.add(x.getName());
            });
        }
        envList.setItems(values);
    }

    /**
     * 环境列表选中方法
     */
    @FXML
    private void onSelect() {
        try {
            String profile = envList.getSelectionModel().getSelectedItem();
            redisClusterService = ServiceFactory.getService(RedisClusterService.class);
            redisClusterService.initRedisClusterConnection(profile);
            initRedisKeyList();
        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("错误");
            errorAlert.setHeaderText("连接失败");
            errorAlert.setContentText(e.getMessage());
            errorAlert.show();
        }
    }

    /**
     * 从redis集群查询指定key的值
     */
    @FXML
    private void queryFromRedisCluster() {
        String redisKey = cacheKey.getText();
        query(redisKey);
    }

    /**
     * 查询redis key列表
     */
    @FXML
    private void searchByKeyword() {
        String key = searchKeyword.getText();
        ObservableList<String> currValues = FXCollections.observableArrayList();
        List<String> currValueList = keyList.stream().filter(x ->
                x.toLowerCase().indexOf(key.toLowerCase()) >= 0).collect(Collectors.toList());
        currValues.addAll(currValueList);
        cacheKeyList.setItems(currValues);
        totalKey.setText(String.valueOf(currValues.size()));
    }

    /**
     * 选中redis key
     */
    @FXML
    private void selectKey() {
        String key = cacheKeyList.getSelectionModel().getSelectedItem();
        if(key == null || "".equals(key)) {
            return;
        }
        query(key);
    }

    /**
     * 查询redis值
     *
     * @param redisKey
     */
    private void query(String redisKey) {
        String type = redisClusterService.type(redisKey);
        cacheKey.setText(redisKey);
        cacheTTL.setText(String.valueOf(redisClusterService.ttl(redisKey)));
        if("string".equals(type)) {
            switchToTextArea();
            valueTextArea.setText(redisClusterService.getString(redisKey));
            valueTextArea.setWrapText(true);
            return;
        }

        if("hash".equals(type)) {
            switchToTableView();
            ObservableList<TableColumn> tableColumnList = valueTableView.getColumns();
            tableColumnList.get(0).setCellValueFactory(new PropertyValueFactory<>("row"));
            tableColumnList.get(1).setCellValueFactory(new PropertyValueFactory<>("key"));
            tableColumnList.get(2).setCellValueFactory(new PropertyValueFactory<>("value"));

            Map<String, String> hashMap = redisClusterService.getHashMap(redisKey);
            ObservableList<HashMapData> data = FXCollections.observableArrayList();
            int i = 1;
            for(String key : hashMap.keySet()) {
                HashMapData hashMapData = new HashMapData();
                hashMapData.setRow(String.valueOf(i ++));
                hashMapData.setKey(key);
                hashMapData.setValue(hashMap.get(key));
                data.add(hashMapData);
            }
            valueTableView.setItems(data);
        }
    }

    @FXML
    private void newConnection() {
        Stage stage = new Stage();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/NewConnection.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("新建连接");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteKey() {
        String key = cacheKey.getText();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认");
        alert.setHeaderText("确定删除吗");
        alert.setContentText("key: " + key);
        Optional<ButtonType> optional = alert.showAndWait();
        if (optional.get() == ButtonType.OK){
            redisClusterService.del(key);
            cacheKey.setText("");
            cacheTTL.setText("");
            disableValue();
            initRedisKeyList();
        }
    }

    /**
     * 初始化redis key list
     */
    private void initRedisKeyList() {
        List<String> keys = redisClusterService.getAllKeys();
        keyList = FXCollections.observableArrayList();
        for(Object key : keys) {
            keyList.add(key.toString());
        }
        String key = searchKeyword.getText();
        if(key != null && !"".equals(key)) {
            searchByKeyword();
            return;
        }
        cacheKeyList.setItems(keyList);
        totalKey.setText(String.valueOf(keyList.size()));
    }

    /**
     * 转换值显示区域为textArea
     */
    private void switchToTextArea() {
        valueTextArea.setDisable(false);
        valueTextArea.setOpacity(1);
        valueTableView.setDisable(true);
        valueTableView.setOpacity(0);
    }

    /**
     * 转换值显示区域为tableView
     */
    private void switchToTableView() {
        valueTextArea.setDisable(true);
        valueTextArea.setOpacity(0);
        valueTableView.setDisable(false);
        valueTableView.setOpacity(1);
    }

    /**
     * 隐藏值区域
     */
    private void disableValue() {
        valueTextArea.setDisable(true);
        valueTextArea.setOpacity(0);
        valueTableView.setDisable(true);
        valueTableView.setOpacity(0);
    }
}
