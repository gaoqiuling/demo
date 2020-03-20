package com.itisacat.com.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class AppPropertiesListener implements ApplicationListener<ApplicationPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        System.out.println("!11");


    }





    CREATE TABLE cc_group_rank_week_1 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_2 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_3 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_4 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_5 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_6 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_7 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_8 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_9 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_10 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_11 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_12 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_13 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_14 like cc_group_rank_week_0;
    CREATE TABLE cc_group_rank_week_15 like cc_group_rank_week_0;
}
