package com.softactive.view;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;

import com.softactive.taxreturn.object.TaxReturnConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataTable<P> implements TaxReturnConstants{
	private List<P> filtered;
	protected List<P> source;
	private List<P> selected;

	public DataTable(List<P> source) {
		this.source = source;
		filtered = new ArrayList<>();
		selected = new ArrayList<>();
	}

	public DataTable(List<P> source, List<P> selected) {
		this.source = source;
		this.selected = selected;
		filtered = new ArrayList<>();
	}

	public DataTable() {
		source = new ArrayList<>();
		filtered = new ArrayList<>();
		selected = new ArrayList<>();
		}

	public void onTransfer(TransferEvent event) {
		StringBuilder builder = new StringBuilder();
		for (Object item : event.getItems()) {
			builder.append(item.toString()).append("<br />");
		}

		FacesMessage msg = new FacesMessage();
		msg.setSeverity(FacesMessage.SEVERITY_INFO);
		msg.setSummary("Items Transferred");
		msg.setDetail(builder.toString());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onSelect(SelectEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Selected", event.getObject().toString()));
	}

	public void onUnselect(UnselectEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Unselected", event.getObject().toString()));
	}

	public void onReorder() {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "List Reordered", null));
	}
}
