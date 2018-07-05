package com.dataset.management.entity;


import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
@Component
public class User implements Serializable {
  private Long id;//id唯一标识
  private String username;//用户名
  private String password;//密码
  private String email;//邮箱
  private String token;//验证码
  private String token_exptime;//验证码有效期
  private Long status;//激活状态
  private String regtime;//注册时间
  //用于加密的盐值
  private String salt;

  private String gender; //性别

  private String region; //地区

  private String birthday; //生日

  private String career; //职业

  @Override
  public String toString() {
    return
            "id=" + id + "#" +
            "username=" + username + "#" +
            "password=" + password + "#" +
            "email=" + email + "#" +
            "gender=" + gender + "#" +
            "region=" + region + "#" +
            "birthday=" + birthday + "#" +
            "career=" + career
            ;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getCareer() {
    return career;
  }

  public void setCareer(String career) {
    this.career = career;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  //用户头像路径
  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  private String imagePath;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getToken_exptime() {
    return token_exptime;
  }

  public void setToken_exptime(String token_exptime) {
    this.token_exptime = token_exptime;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public String getRegtime() {
    return regtime;
  }

  public void setRegtime(String regtime) {
    this.regtime = regtime;
  }
}
