using System;
using System.ComponentModel.Composition;
using System.Diagnostics;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Ascon.Pilot.DataClasses;
using Ascon.Pilot.DataModifier;
using Ascon.Pilot.ServerExtensions.SDK;
using Newtonsoft.Json;

[Export(typeof(IServerActivity))]
public class SendNotifications : IServerActivity
{
    private static readonly HttpClient client = new HttpClient { Timeout = TimeSpan.FromSeconds(30) };

    public string Name => "SendNotifications";

    public const string deleState = "deleState";
    public const string srvUrl = "srvUrl";

    public async Task RunAsync(IModifierBase modifier, IModifierBackend backend, IServerActivityContext serverActivityContext, IAutomationEventContext automationEventContext)
    {
        if (!serverActivityContext.Params.TryGetValue(srvUrl, out var SrvUrl))
            return;

        var attr = automationEventContext.Source.Attributes;
        var izmCodeValue = attr.FirstOrDefault(x => x.Key == "izm_code").Value;
        if (izmCodeValue == null)
        {
            Trace.TraceWarning("SendNotifications: у объекта нет атрибута izm_code");
            return;
        }
        string izm_code = izmCodeValue;

        string route;
        string project = "";
        string pilotLogin = "";

        if (serverActivityContext.Params.ContainsKey(deleState))
        {
            /* CHAnge Request */
            route = "/CHAR";
        }
        else
        {
            /* 'C' из CRUD */
            route = "/CREA";

            var projectValue = attr.FirstOrDefault(x => x.Key == "ShortName").Value;
            if (projectValue != null)
                project = projectValue;

            var executorValue = attr.FirstOrDefault(x => x.Key == "izm_executor").Value;
            int[] executor = executorValue;
            if (executor != null && executor.Length > 0)
            {
                INPerson pers_izm_exec = backend.GetPersonOnPosition(executor[0]);
                if (pers_izm_exec != null)
                    pilotLogin = pers_izm_exec.Login;
            }
            if (string.IsNullOrEmpty(pilotLogin))
                Trace.TraceWarning($"SendNotifications: не удалось определить исполнителя для {izm_code}");
        }

        var json = JsonConvert.SerializeObject(new { izm_code, pilotLogin, project });
        var content = new StringContent(json, Encoding.UTF8, "application/json");

        // Ошибки только логируются: недоступность приёмника не должна влиять на работу Pilot
        try
        {
            var response = await client.PostAsync(SrvUrl + route, content);
            if (!response.IsSuccessStatusCode)
                Trace.TraceError($"SendNotifications: {route} {izm_code} -> HTTP {(int)response.StatusCode}");
        }
        catch (Exception ex)
        {
            Trace.TraceError($"SendNotifications: {route} {izm_code} -> {ex.Message}");
        }
    }
}
