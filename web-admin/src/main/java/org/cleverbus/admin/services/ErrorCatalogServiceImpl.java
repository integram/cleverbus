/*
 * Copyright (C) 2015
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cleverbus.admin.services;

import java.util.Map;

import javax.annotation.Resource;

import org.cleverbus.api.exception.ErrorExtEnum;

import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ErrorCatalogService}.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 */
@Service
public class ErrorCatalogServiceImpl implements ErrorCatalogService {

    @Resource
    private Map<String,ErrorExtEnum[]> errorCodesCatalog;

    @Override
    public Map<String, ErrorExtEnum[]> getErrorCatalog() {
        return errorCodesCatalog;
    }
}
