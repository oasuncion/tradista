package finance.tradista.core.common.ui.view;

import java.io.Serializable;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.primefaces.model.menu.Submenu;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/*
 * Copyright 2022 Olivier Asuncion
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.    */

@Named
@SessionScoped
public class MenuView implements Serializable {

	private static final long serialVersionUID = 6546524155625144194L;

	private MenuModel model;

	@PostConstruct
	public void init() {
		model = new DefaultMenuModel();

		Submenu tradeMenu = DefaultSubMenu.builder().label("Trades").build();
		Submenu productMenu = DefaultSubMenu.builder().label("Products").build();
		Submenu configurationMenu = DefaultSubMenu.builder().label("Configuration").build();
		DefaultMenuItem item = DefaultMenuItem.builder().value("GC Repo").url("gcrepotrade.xhtml").build();
		tradeMenu.getElements().add(item);
		item = DefaultMenuItem.builder().value("GC Basket").url("gcbasket.xhtml").build();
		productMenu.getElements().add(item);
		item = DefaultMenuItem.builder().value("Processing Org Defaults").url("processingorgdefaults.xhtml").build();
		configurationMenu.getElements().add(item);
		model.getElements().add(tradeMenu);
		model.getElements().add(productMenu);
		model.getElements().add(configurationMenu);
	}

	public MenuModel getModel() {
		return model;
	}

	public void setModel(MenuModel tradeModel) {
		this.model = tradeModel;
	}

	public String logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().invalidateSession();
		return "/login.xhtml?faces-redirect=true";
	}
}