/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.config.bean;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Data
@Validated
public class NewsProperties {

    @Min(1)
    private int overviewSize;

    @Min(1)
    private int pageSize;

    @Override
    public String toString(){
        return "{\n\"News properties\":{"
            + "\n \"overviewSize\":\"" + overviewSize + "\""
            + "\n \"pageSize\":\"" + pageSize + "\""
            + "\n}\n}";
    }

}
