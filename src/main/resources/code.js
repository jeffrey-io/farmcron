function enable_global_ace_editor(form_id, dest_id) {
  $(document).ready(function() {
    var editor = ace.edit("global_ace_editor");
    editor.setTheme("ace/theme/monokai");
    editor.getSession().setMode("ace/mode/html"); // TODO: make configurable
    $('#' + form_id).on('submit', function(e) {
      $('#' + dest_id).val(editor.getValue());
      this.submit();
    });
  });  
}
