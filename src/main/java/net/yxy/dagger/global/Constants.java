/**
 * Copyright (c) 2016, Xianyi Ye
 *
 * This project includes software developed by Xianyi Ye
 * yexianyi@hotmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.yxy.dagger.global;

public final class Constants {
	
	public static final int SYN_INTERVAL = 5000 ;
	public static final int REFRESH_INTERVAL_MILISEC = 3000 ;
	public static final int REFRESH_INTERVAL = REFRESH_INTERVAL_MILISEC/1000 ;
	public static final String DATATYPES_RSP_QUEUE = "DATATYPES.RSP.QUEUE" ;
	public static final String DB_NAME = "dagger_db" ;
	public static final String DB_PATH= "plocal:./databases/"+Constants.DB_NAME ;
	public static final String DB_USERNAME = "admin" ;
	public static final String DB_PASSWORD = "admin" ;
	public static final int DB_MAX_POOL_SIZE = 50;
	
	//ENTITY CLASS NAME
	public static final String ENTITY_SERVER = "Server" ;

}
