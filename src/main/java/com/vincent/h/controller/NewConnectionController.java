package com.vincent.h.controller;

import com.vincent.h.model.RedisConfig;
import com.vincent.h.service.ConfigService;
import com.vincent.h.service.ServiceFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * @description 
 *
 * @author huangxiaocheng
 * @date 2020/2/25 11:51
 */
public class NewConnectionController {

    @FXML
    private TextField hostAndPort;

    @FXML
    private TextField password;

    @FXML
    private TextField name;

    @FXML
    private ToggleGroup typeRadioGroup;

    @FXML
    private AnchorPane rootLayout;

    /**
     * 初始化方法
     */
    @FXML
    private void initialize() {

    }

    @FXML
    private void submit() {
        RadioButton radioButton = (RadioButton)typeRadioGroup.getSelectedToggle();
        String type = "单机".equals(radioButton.getText()) ? "single" : "cluster";
        RedisConfig redisConfig = new RedisConfig();
        redisConfig.setName(name.getText());
        redisConfig.setHostAndPort(hostAndPort.getText());
        redisConfig.setPassword(password.getText());
        redisConfig.setType(type);
        ConfigService configService = ServiceFactory.getService(ConfigService.class);
        boolean isSuccess = configService.andConfig(redisConfig);
        if(isSuccess) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText("操作成功");
            alert.show();
            cancel();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText("操作失败");
        alert.setContentText("连接名已存在");
        alert.showAndWait();
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage)rootLayout.getScene().getWindow();
        stage.close();
    }
}
